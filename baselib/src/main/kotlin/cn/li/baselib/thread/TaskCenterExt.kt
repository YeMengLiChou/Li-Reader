package cn.li.baselib.thread

import java.util.concurrent.Future

/**
 * 基于 [TaskCenter] 的扩展函数和顶层函数
 *
 * @author Grimrise 2024/10/8
 */

/**
 * kotlin lambda 方式创建 [Task]
 * */
internal fun (() -> Unit).toTask(taskName: String): Task {
    return object : Task(taskName) {
        override fun execute() {
            invoke()
        }
    }
}

/**
 * top-level 提交任务到通用线程池
 * */
fun submitTask(taskName: String, callable: () -> Unit): Pair<Task, Future<*>?> {
    return TaskCenter.submitTask(taskName, callable)
}

/**
 * lambda 扩展，提交任务到通用线程池
 * */
fun TaskCenter.submitTask(taskName: String, callable: () -> Unit): Pair<Task, Future<*>?> {
    val task = callable.toTask(taskName)
    return Pair(task, submitTask(task))
}


/**
 * top-level, 提交任务到主线程
 * */
fun submitMainTask(taskName: String, callable: () -> Unit): Task {
    return TaskCenter.submitMainTask(taskName, immediately = false, callable)
}

/**
 * lambda 扩展，提交任务到主线程
 * */
fun TaskCenter.submitMainTask(taskName: String, immediately: Boolean = false, callable: () -> Unit): Task {
    val task = callable.toTask(taskName)
    submitMainTask(task, immediately)
    return task
}

/**
 * top-level, 定时提交任务到通用线程池
 * */
fun submitTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    return TaskCenter.submitTaskDelayed(taskName, delay, callable)
}

/**
 * lambda 扩展，定时提交任务到通用线程池
 * */
fun TaskCenter.submitTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    val task = callable.toTask(taskName)
    submitTaskDelayed(task, delay)
    return task
}


/**
 * top-level, 定时提交任务到主线程
 * */
fun submitMainTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    return TaskCenter.submitMainTaskDelayed(taskName, delay, callable)
}

/**
 * lambda 扩展，定时提交任务到主线程
 * */
fun TaskCenter.submitMainTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    val task = callable.toTask(taskName)
    submitMainTaskDelayed(task, delay)
    return task
}


/**
 * top-level, 提交任务到 IO 线程池
 * */
fun submitIOTask(taskName: String, callable: () -> Unit): Pair<Task, Future<*>?> {
    return TaskCenter.submitIOTask(taskName, callable)
}

/**
 * lambda 扩展，提交任务到 IO 线程池
 * */
fun TaskCenter.submitIOTask(taskName: String, callable: () -> Unit): Pair<Task, Future<*>?> {
    val task = callable.toTask(taskName)
    return Pair(task, submitIOTask(task))
}

/**
 * top-level, 定时提交任务到 IO 线程池
 * */
fun submitIOTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    return TaskCenter.submitIOTaskDelayed(taskName, delay, callable)
}

/**
 * lambda 扩展，定时提交任务到 IO 线程池
 * */
fun TaskCenter.submitIOTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    val task = callable.toTask(taskName)
    submitIOTaskDelayed(task, delay)
    return task
}

/**
 * top-level, 提交任务到 Serial 线程池
 * */
fun submitSerialTask(taskName: String, callable: () -> Unit): Pair<Task, Future<*>?> {
    return TaskCenter.submitSerialTask(taskName, callable)
}

/**
 * lambda 扩展，提交任务到 Serial 线程池
 * */
fun TaskCenter.submitSerialTask(taskName: String, callable: () -> Unit): Pair<Task, Future<*>?> {
    val task = callable.toTask(taskName)
    return Pair(task, submitSerialTask(task))
}

/**
 * top-level, 定时提交任务到 Serial 线程池
 * */
fun submitSerialTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    return TaskCenter.submitSerialTaskDelayed(taskName, delay, callable)
}

/**
 * lambda 扩展，定时提交任务到 Serial 线程池
 * */
fun TaskCenter.submitSerialTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    val task = callable.toTask(taskName)
    submitSerialTaskDelayed(task, delay)
    return task
}

/**
 * top-level, 提交任务到 Background 线程池
 * */
fun submitBackgroundTask(taskName: String, callable: () -> Unit): Pair<Task, Future<*>?> {
    return TaskCenter.submitBackgroundTask(taskName, callable)
}

/**
 * lambda 扩展，提交任务到 Background 线程池
 * */
fun TaskCenter.submitBackgroundTask(taskName: String, callable: () -> Unit): Pair<Task, Future<*>?> {
    val task = callable.toTask(taskName)
    return Pair(task, submitBackgroundTask(task))
}

/**
 * top-level, 定时提交任务到 Background 线程池
 * */
fun submitBackgroundTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    return TaskCenter.submitBackgroundTaskDelayed(taskName, delay, callable)
}

/**
 * lambda 扩展，定时提交任务到 Background 线程池
 * */
fun TaskCenter.submitBackgroundTaskDelayed(taskName: String, delay: Long, callable: () -> Unit): Task {
    val task = callable.toTask(taskName)
    submitBackgroundTaskDelayed(task, delay)
    return task
}

/**
 * top-level, 提交 Idle 任务
 * */
fun submitIdleTask(taskName: String, timeout: Long = IdleTask.TIMEOUT_NOT_SET, callable: () -> Unit): IdleTask<Unit> {
    return TaskCenter.submitIdleTask(taskName, timeout, callable)
}

/**
 * lambda 扩展，提交 Idle 任务
 * */
fun TaskCenter.submitIdleTask(taskName: String, timeout: Long = IdleTask.TIMEOUT_NOT_SET, callable: () -> Unit): IdleTask<Unit> {
    val task = object: IdleTask<Unit>(taskName, timeout) {
        override fun onSuccess(result: Unit?) {
            //
        }

        override fun onFailed(e: Throwable) {
            //
        }

        override fun execute(): Unit {
            callable.invoke()
        }
    }
    submitIdleTask(task)
    return task
}