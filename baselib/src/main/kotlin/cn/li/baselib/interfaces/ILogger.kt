package cn.li.baselib.interfaces

/**
 *
 *
 * @author Grimrise 2024/10/8
 */
interface ILogger {

    fun debug(tag: String, msg: String)
    fun debug(tag: String, msg: String, throwable: Throwable)

    fun verbose(tag: String, msg: String)
    fun verbose(tag: String, msg: String, throwable: Throwable)

    fun info(tag: String, msg: String)
    fun info(tag: String, msg: String, throwable: Throwable)

    fun warn(tag: String, msg: String)
    fun warn(tag: String, msg: String, throwable: Throwable)

    fun error(tag: String, msg: String)
    fun error(tag: String, msg: String, throwable: Throwable)
}