package cn.li.baselib.thread



/**
 * 带有成功回调 [onSuccess] 和 失败回调 [onFailed] 的任务
 * */
abstract class CallbackTask<T>(
    taskName: String,
    val callbackInMain: Boolean = false
): ThreadTask<T>(taskName) {



    override fun callback(result: T?, e: Throwable?) {
        if (callbackInMain) {
            TaskCenter.submitMainTask("$taskName-CallbackMainTask") {
                callbackReal(result, e)
            }
        } else {
            callbackReal(result, e)
        }
    }

    private fun callbackReal(result: T?, e: Throwable?) {
        if (e != null) {
            onFailed(e)
        } else {
            onSuccess(result)
        }
    }

    abstract fun onSuccess(result: T?)

    abstract fun onFailed(e: Throwable)

}

