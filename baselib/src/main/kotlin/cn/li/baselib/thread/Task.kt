package cn.li.baselib.thread

/**
 * 没有回调的任务
 * */
abstract class Task(taskName: String): ThreadTask<Unit>(taskName) {

    final override fun callback(result: Unit?, e: Throwable?) {
        // ignore
        if (e != null) {
            throw e
        }
    }
}


