package cn.li.baselib.thread

import android.os.Looper

/**
 *
 *
 * @author Grimrise 2024/10/8
 */

/**
 * 是否为主线程
 * */
fun isMainThread() = Thread.currentThread() == Looper.getMainLooper().thread

