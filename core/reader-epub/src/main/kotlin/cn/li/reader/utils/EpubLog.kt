package cn.li.reader.utils

import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date

/**
 *
 *
 * @author Grimrise 2024/9/25
 */

object EpubLog {

    private var logger: ILogger? = null

    fun setLogger(logger: ILogger) {
        this.logger = logger
    }

    private fun getLogger(): ILogger {
        if (logger == null) {
            synchronized(this) {
                if (logger == null) {
                    logger = if (System.getProperty("os.name")?.contains("android", ignoreCase = true) == true) {
                        AndroidLogger()
                    } else {
                        DefaultLogger()
                    }
                }
            }
        }
        return logger!!
    }

    fun d(tag: String, msg: String) {
        getLogger().debug(tag, msg)
    }

    fun i(tag: String, msg: String) {
        getLogger().info(tag, msg)
    }

    fun w(tag: String, msg: String) {
        getLogger().warn(tag, msg)
    }

    fun e(tag: String, msg: String) {
        getLogger().error(tag, msg)
    }

    fun e(tag: String, msg: String, throwable: Throwable) {
        getLogger().error(tag, msg, throwable)
    }

    interface ILogger {
        fun debug(tag: String, msg: String)
        fun info(tag: String, msg: String)
        fun warn(tag: String, msg: String)
        fun error(tag: String, msg: String)
        fun error(tag: String, msg: String, throwable: Throwable)
    }

    private class AndroidLogger: ILogger {
        override fun debug(tag: String, msg: String) {
            Log.d(tag, msg)
        }

        override fun info(tag: String, msg: String) {
            Log.i(tag, msg)
        }

        override fun warn(tag: String, msg: String) {
            Log.w(tag, msg)
        }

        override fun error(tag: String, msg: String) {
            Log.e(tag, msg)
        }

        override fun error(tag: String, msg: String, throwable: Throwable) {
            Log.e(tag, msg, throwable)
        }
    }

    private class DefaultLogger: ILogger {

        private fun getDate(): String {
            return SimpleDateFormat.getDateInstance().format(Date(System.currentTimeMillis()))
        }
        override fun debug(tag: String, msg: String) {
            println("${getDate()} [debug]-$tag $msg")
        }

        override fun info(tag: String, msg: String) {
            println("${getDate()} [info]-$tag $msg")
        }

        override fun warn(tag: String, msg: String) {
            println("${getDate()} [warn]-$tag $msg")
        }

        override fun error(tag: String, msg: String) {
            println("${getDate()} [error]-$tag $msg")
        }

        override fun error(tag: String, msg: String, throwable: Throwable) {
            val sw = StringWriter()
            val pw = PrintWriter(sw).also { throwable.printStackTrace(it) }
            println("${getDate()} [error]-$tag $msg\n\t$pw")
        }
    }

}