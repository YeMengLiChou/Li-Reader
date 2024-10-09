package cn.li.baselib.thread

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.MessageQueue
import android.os.MessageQueue.IdleHandler
import android.os.Process
import android.os.SystemClock
import cn.li.baselib.utils.Logger
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


object TaskCenter {

    const val TAG = "TaskCenter"

    private const val EXECUTOR_MAIN = 1
    private const val EXECUTOR_NORMAL = 2
    private const val EXECUTOR_IO = 3
    private const val EXECUTOR_BACKGROUND = 4
    private const val EXECUTOR_SERIAL = 5

    private val PROCESS_COUNT = Runtime.getRuntime().availableProcessors().coerceAtLeast(2)
    private val DEFAULT_CORE_POOL_SIZE = PROCESS_COUNT + 1
    private val MAX_DEFAULT_THREAD_SIZE = DEFAULT_CORE_POOL_SIZE * 2
    private const val IO_CORE_POOL_SIZE = 0
    private const val MAX_IO_THREAD_SIZE = 128
    private const val KEEP_ALIVE_SECONDS = 30L

    private val NORMAL_THREAD_FACTORY = DefaultThreadFactory("NormalExecutor")
    private val IO_THREAD_FACTORY = DefaultThreadFactory("IOExecutor")
    private val BACKGROUND_THREAD_FACTORY = BackgroundThreadFactory

    // 默认拒绝策略，正常不会触发（除非在高压场景）
    private val DEFAULT_REJECT_POLICY = RejectedExecutionHandler { r, executor ->
        Executors.newCachedThreadPool().submit(r)
        Logger.error(TAG, "Rejection Occurred！${executor}")
    }

    // 线程池提交的任务
    private val taskFutures = ConcurrentHashMap<ThreadTask<*>, Future<*>>()

    // 主线程
    internal val mainExecutor = Handler(Looper.getMainLooper())
    // 通用/CPU cpu
    internal val normalExecutor = ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE, MAX_DEFAULT_THREAD_SIZE,
        KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, LinkedBlockingQueue(), NORMAL_THREAD_FACTORY, DEFAULT_REJECT_POLICY).apply { allowCoreThreadTimeOut(true) }
    // io 密集
    internal val ioExecutor = ThreadPoolExecutor(IO_CORE_POOL_SIZE, MAX_IO_THREAD_SIZE,
        KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, SynchronousQueue(), IO_THREAD_FACTORY, DEFAULT_REJECT_POLICY).apply { allowCoreThreadTimeOut(true) }
    // 串行回调
    internal val serialExecutor = ThreadPoolExecutor(1, 1, KEEP_ALIVE_SECONDS,
        TimeUnit.SECONDS, LinkedBlockingQueue()).apply { allowCoreThreadTimeOut(true) }
    // 后台回调
    internal val backgroundExecutor = ThreadPoolExecutor(0, 3, 15L,
        TimeUnit.SECONDS, LinkedBlockingQueue(256), BACKGROUND_THREAD_FACTORY, DEFAULT_REJECT_POLICY).apply { allowCoreThreadTimeOut(true) }

    // 负责定时提交任务
    private val delayHandler = Handler(
        HandlerThread("TaskCenter-DelayHandler").also { it.start() }.looper,
    ) { msg ->
        val target = msg.arg1
        val task = msg.obj as? ThreadTask<*>

        if (task == null) {
            Logger.error(TAG, "Task in delayed message is null!")
            return@Handler true
        }
        when(target) {
            EXECUTOR_NORMAL -> submitTask(task)
            EXECUTOR_IO -> submitIOTask(task)
            EXECUTOR_BACKGROUND -> submitBackgroundTask(task)
            EXECUTOR_SERIAL -> submitSerialTask(task)
        }
        true
    }

    private class DefaultThreadFactory(private val tag: String): ThreadFactory {

        private val mCounter = AtomicInteger(0)
        private val mThreadGroup = System.getSecurityManager()?.threadGroup ?: Thread.currentThread().threadGroup

        override fun newThread(r: Runnable?): Thread {
            val name = "$tag-Thread-${mCounter.incrementAndGet()}"
            return Thread(mThreadGroup, r, name).apply {
                if (isDaemon) {
                    isDaemon = false
                }
                if (priority != Thread.NORM_PRIORITY) {
                    priority = Thread.NORM_PRIORITY
                }
            }
        }
    }

    private object BackgroundThreadFactory: ThreadFactory {

        class BackgroundThread @JvmOverloads constructor(
            group:ThreadGroup?, target:Runnable?, name:String, stackSize:Long = 0
        ): Thread(group, target, name, stackSize) {
            override fun start() {
                Process.setThreadPriority(Process.myTid(), Process.THREAD_PRIORITY_BACKGROUND)
                super.start()
            }
        }

        private val mCounter = AtomicInteger(0)
        private val mThreadGroup = System.getSecurityManager()?.threadGroup ?: Thread.currentThread().threadGroup

        override fun newThread(r: Runnable?): Thread {
            val name = "BackgroundExecutor-Thread-${mCounter.incrementAndGet()}"
            return BackgroundThread(mThreadGroup, r, name).apply {
                if (isDaemon) {
                    isDaemon = false
                }
                if (priority != Thread.MIN_PRIORITY) {
                    priority = Thread.MIN_PRIORITY
                }
            }
        }
    }

    // 主线程 mq 反射访问
    private var mqMessagesField: Field? = null
    // 空消息
    private var notifyTask: Task? = null

    private val idleTasks = LinkedBlockingQueue<IdleTask<*>>()
    // 主线程空闲回调
    internal val idleHandler = IdleHandler {
        if (idleTasks.isNotEmpty() && isMQIdle(idleTaskThreshold)) { // 满足空闲，则执行
            val task = idleTasks.poll()
            task?.apply {
                if (timeoutTask != null) {
                    cancelTask(timeoutTask!!, true)
                    timeoutTask = null
                }
                call()
            }

            // 防止主线程堵塞在 nativePollOnce，发送一个空消息执行
            if (idleTasks.isNotEmpty() && isMQIdle(idleTaskThreshold)) {
                if (notifyTask == null) {
                    notifyTask = fun(){ /* empty */ }.toTask("NotifyTask")
                }
                submitMainTask(notifyTask!!, immediately = true)
            }
        }
        true
    }.also { mainExecutor.looper.queue.addIdleHandler(it) }


    var isDebug = false

    // 主线程 mq 允许执行 idle 任务的间隔时间
    var idleTaskThreshold = 5L

    @SuppressLint("PrivateApi")
    private fun isMQIdle(threshold: Long): Boolean {
        if (mqMessagesField == null) {
            try {
                mqMessagesField = MessageQueue::class.java.getDeclaredField("mMessages").also {
                    it.isAccessible = true
                }
            } catch (e: Exception) {
                Logger.error(TAG, "Failed to get mMessages field")
            }
        }
        if (mqMessagesField != null) {
            val mMessage = mqMessagesField?.get(Looper.getMainLooper().queue) as? Message
            // mq 没有消息
            return mMessage == null
                    // 没有异步消息，同时下一个消息的间隔满足 threshold
                    || (!mMessage.isAsynchronous && mMessage.`when` >= SystemClock.uptimeMillis() + threshold)
        }
        return false
    }


    /**
     * 提交任务到通用线程池，带 callback 的任务会在 ***子线程*** 回调
     * */
    fun <T> submitTask(task: ThreadTask<T>): Future<*>? {
        return submitTask(task, normalExecutor)
    }

    /**
     * 定时提交任务到通用线程池，带 callback 的任务会在 ***子线程*** 回调
     * @param delay ms
     * */
    fun <T> submitTaskDelayed(task: ThreadTask<T>, delay: Long) {
        sendMessageDelayed(task, EXECUTOR_NORMAL, delay)
    }

    /**
     * 提交任务到主线程中，带 callback 的任务会在 ***主线程*** 回调
     * @param immediately 立即回调
     * */
    fun <T> submitMainTask(task: ThreadTask<T>, immediately: Boolean = false) {
        val runnable = Runnable {
            task.call()
        }
        if (immediately) {
            mainExecutor.postAtFrontOfQueue(runnable)
        } else {
            mainExecutor.post(runnable)
        }
    }

    /**
     * 定时提交任务到主线程，带 callback 的任务会在 ***主线程*** 回调
     * @param delay ms
     * */
    fun <T> submitMainTaskDelayed(task: ThreadTask<T>, delay: Long) {
        sendMessageDelayed(task, EXECUTOR_MAIN, delay)
    }

    /**
     * 提交任务到 IO线程池，带 callback 的任务会在 ***子线程*** 回调
     * */
    fun <T> submitIOTask(task: ThreadTask<T>): Future<*>? {
        return submitTask(task, ioExecutor)
    }

    /**
     * 定时提交任务到 IO线程池，带 callback 的任务会在 ***子线程*** 回调
     * @param delay ms
     *
     * */
    fun <T> submitIOTaskDelayed(task: ThreadTask<T>, delay: Long) {
        sendMessageDelayed(task, EXECUTOR_IO, delay)                                            
    }

    /**
     * 定时提交任务到 串行线程池，带 callback 的任务会在 ***子线程*** 回调
     *
     * */
    fun <T> submitSerialTask(task: ThreadTask<T>): Future<*>? {
        return submitTask(task, serialExecutor)
    }

    /**
     * 定时提交任务到 串行线程池，带 callback 的任务会在 ***子线程*** 回调
     * @param delay ms
     *
     * */
    fun <T> submitSerialTaskDelayed(task: ThreadTask<T>, delay: Long) {
        sendMessageDelayed(task, EXECUTOR_SERIAL, delay)
    }

    /**
     * 提交任务到 后台线程池，带 callback 的任务会在 ***子线程*** 回调
     * */
    fun <T> submitBackgroundTask(task: ThreadTask<T>): Future<*>? {
        return submitTask(task, backgroundExecutor)
    }

    /**
     * 定时提交任务到 后台线程池，带 callback 的任务会在 ***子线程*** 回调
     * @param delay ms
     *
     * */
    fun <T> submitBackgroundTaskDelayed(task: ThreadTask<T>, delay: Long) {
        sendMessageDelayed(task, EXECUTOR_BACKGROUND, delay)
    }

    /**
     * 提交任务到 主线程空闲队列，带 callback 的任务会在 ***主线程*** 回调
     *
     * *不建议添加耗时任务，会延迟后续的主线程核心任务，如 doFrame、ViewAttach 等*
     * */
    fun <T> submitIdleTask(task: IdleTask<T>): Boolean {
        val timeout = task.timeout
        if (timeout != IdleTask.TIMEOUT_NOT_SET) {
            val timeoutTask = fun() {
                // 超时后直接在主线程中执行
                if (idleTasks.remove(task)) {
                    task.call()
                }
            }.toTask("${task.taskName}-TimeoutTask")
            task.timeoutTask = timeoutTask
            submitMainTaskDelayed(timeoutTask, timeout)
        }
        return idleTasks.offer(task)
    }


    /**
     * 取消任务，在线程池中提交的任务可以使用 [force] 停止正在执行的任务
     * @return 是否已经取消该任务
     * */
    fun <T> cancelTask(task: ThreadTask<T>, force: Boolean): Boolean {
        if (task.isCancelled) {
            Logger.debug(TAG, "cancelTask: [${task.taskName}] already cancelled")
            return true
        }
        task.isCancelled = true

        if (delayHandler.hasMessages(task.hashCode())) {
            delayHandler.removeMessages(task.hashCode())
            Logger.debug(TAG, "cancelTask: [${task.taskName}] in message queue")
            return true
        }

        if (task is IdleTask && idleTasks.contains(task)) {
            idleTasks.remove(task)
            task.timeoutTask?.let { cancelTask(it, force) }
            Logger.debug(TAG, "cancelTask: [${task.taskName}] in idle queue")
            return true
        }

        try {
            if (taskFutures.containsKey(task)) {
                val future = taskFutures.remove(task)
                future!!.cancel(force)
                Logger.debug(TAG, "cancelTask: [${task.taskName}] in executor")
                return true
            }
        } catch (e: Exception) {
            Logger.error(TAG, "cancelTask [${task.taskName}] failed: ${e.message}", e)
            if (isDebug) {
                throw e
            }
        }
        return false
    }

    /**
     * 移除线程池提交的任务，并非取消任务，若需要取消任务请用 [cancelTask]
     * */
    fun <T> removeTask(task: ThreadTask<T>): Future<*>? {
        return taskFutures.remove(task)
    }


    private fun <T> submitTask(task: ThreadTask<T>, executor: ExecutorService): Future<*>? {
        return executor.submit(task)?.also {
            taskFutures[task] = it
        }
    }

    private fun sendMessageDelayed(task: ThreadTask<*>, target: Int, delay: Long) {
        val msg = Message.obtain().apply {
            obj = task
            what = task.hashCode()
            arg1 = target
        }
        delayHandler.sendMessageDelayed(msg, delay)
    }


}
