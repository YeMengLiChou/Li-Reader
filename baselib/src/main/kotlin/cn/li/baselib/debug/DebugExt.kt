package cn.li.baselib.debug

import android.os.SystemClock

/**
 *  统计 [block] 执行时间
 *
 *  @return 执行时间 ms
 *  @author Grimrise 2024/9/22
 */
inline fun measureTime(crossinline block: () -> Unit): Long {
    val startTime = SystemClock.elapsedRealtime()
    block()
    return SystemClock.elapsedRealtime() - startTime
}