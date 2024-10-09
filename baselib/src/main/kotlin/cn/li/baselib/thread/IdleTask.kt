package cn.li.baselib.thread

/**
 *
 *
 * @author Grimrise 2024/10/9
 */
abstract class IdleTask<T>(
    taskName: String,
    val timeout: Long = TIMEOUT_NOT_SET
): CallbackTask<T>(taskName) {

    internal var timeoutTask: Task? = null

    companion object {
        const val TIMEOUT_NOT_SET = -1L
    }
}