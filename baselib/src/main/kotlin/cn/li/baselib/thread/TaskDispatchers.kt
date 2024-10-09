package cn.li.baselib.thread

import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher

/**
 * 用于协程的 Dispatcher，共用线程池
 * */
object TaskDispatchers {
    val Main = TaskCenter.mainExecutor.asCoroutineDispatcher("TaskCenter-Dispatcher-ActivityMain")
    val IO = TaskCenter.ioExecutor.asCoroutineDispatcher()
    val Default = TaskCenter.normalExecutor.asCoroutineDispatcher()
    val Serial = TaskCenter.serialExecutor.asCoroutineDispatcher()
    val Background = TaskCenter.backgroundExecutor.asCoroutineDispatcher()
}
