package cn.li.baselib.utils

import android.util.Log
import cn.li.baselib.interfaces.ILogger

object Logger: ILogger {

    private var mLogger: ILogger? = null

    fun setLogger(logger: ILogger) {
        if (mLogger == null) {
            synchronized(this) {
                if (mLogger == null) {
                    mLogger = logger
                }
            }
        }
    }

    fun getLogger() = mLogger ?: AndroidLogger


    override fun debug(tag: String, msg: String) {
        getLogger().debug(tag, msg)
    }

    override fun debug(tag: String, msg: String, throwable: Throwable) {
        getLogger().debug(tag, msg, throwable)
    }

    override fun verbose(tag: String, msg: String) {
        getLogger().verbose(tag, msg)
    }

    override fun verbose(tag: String, msg: String, throwable: Throwable) {
        getLogger().verbose(tag, msg, throwable)
    }

    override fun info(tag: String, msg: String) {
        getLogger().info(tag, msg)
    }

    override fun info(tag: String, msg: String, throwable: Throwable) {
        getLogger().info(tag, msg, throwable)
    }

    override fun warn(tag: String, msg: String) {
        getLogger().warn(tag, msg)
    }

    override fun warn(tag: String, msg: String, throwable: Throwable) {
        getLogger().warn(tag, msg, throwable)
    }

    override fun error(tag: String, msg: String) {
        getLogger().error(tag, msg)
    }

    override fun error(tag: String, msg: String, throwable: Throwable) {
        getLogger().error(tag, msg, throwable)
    }

    private val AndroidLogger = object : ILogger {
        override fun debug(tag: String, msg: String) {
            Log.d(tag, msg)
        }

        override fun debug(tag: String, msg: String, throwable: Throwable) {
            Log.d(tag, msg, throwable)
        }

        override fun verbose(tag: String, msg: String) {
            Log.v(tag, msg)
        }

        override fun verbose(tag: String, msg: String, throwable: Throwable) {
            Log.v(tag, msg, throwable)
        }

        override fun info(tag: String, msg: String) {
            Log.i(tag, msg)
        }

        override fun info(tag: String, msg: String, throwable: Throwable) {
            Log.i(tag, msg, throwable)
        }

        override fun warn(tag: String, msg: String) {
            Log.w(tag, msg)
        }

        override fun warn(tag: String, msg: String, throwable: Throwable) {
            Log.w(tag, msg, throwable)
        }

        override fun error(tag: String, msg: String) {
            Log.e(tag, msg)
        }

        override fun error(tag: String, msg: String, throwable: Throwable) {
            Log.e(tag, msg, throwable)
        }
    }

}
