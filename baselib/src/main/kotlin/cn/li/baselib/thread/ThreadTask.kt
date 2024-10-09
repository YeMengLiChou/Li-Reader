package cn.li.baselib.thread

import android.os.SystemClock
import cn.li.baselib.utils.Logger
import java.util.concurrent.Callable


/**
 *
 *
 * @author Grimrise 2024/10/8
 */
abstract class ThreadTask <T>(
    val taskName: String = "ThreadTask"
): Callable<T> {

    companion object {
        @JvmStatic
        val TAG = TaskCenter.TAG
        @JvmStatic
        val isDebug get() = TaskCenter.isDebug
    }

    @Volatile
    var isCancelled = false
        internal set

    @Volatile
    var isRunning = false
        internal set

    @Volatile
    var isCompleted = false
        internal set

    override fun call(): T? {
        val startTime = SystemClock.elapsedRealtime()
        Logger.debug(TAG, "Task[$taskName] started")

        if (isRunning) {
            Logger.debug(TAG, "Task[$taskName] already running")
            return null
        }
        isRunning = true

        var result: T? = null
        try {
            if (!isCancelled) {
                result = execute()

                val future = TaskCenter.removeTask(this)
                if (future != null) {
                    // 已经取消，不进行回调
                    if (future.isCancelled || isCancelled) {
                        Logger.debug(TAG, "Task[$taskName] cancelled")
                    } else {
                        Logger.debug(TAG, "Task[$taskName] finished")
                        isCompleted = true
                        callback(result, null)
                    }
                } else {
                    // 在主线程的mq中执行
                    Logger.debug(TAG, "Task[$taskName] finished in message queue")
                    isCompleted = true
                    callback(result, null)
                }
            }
        } catch (e: Exception) {
            Logger.error(TAG, "Task[$taskName] failed caused by ${e.message}", e)
            callback(null, e)
        } finally {
            isRunning = false
            if (isDebug) {
                Logger.debug(TAG, "Task[$taskName] cost: ${SystemClock.elapsedRealtime() - startTime}")
            }
        }
        return result
    }

    abstract fun execute(): T

    abstract fun callback(result: T?, e: Throwable?)


    override fun toString(): String {
        return "ThreadTask(${taskName}, isRunning=$isRunning, isCancelled=$isCancelled, isCompleted=$isCompleted)@${hashCode()}"
    }
}






