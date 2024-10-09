package cn.li.baselib.bus

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER
)
@IntDef(value = [ThreadMode.MAIN, ThreadMode.SYNC, ThreadMode.ASYNC])
annotation class ThreadMode {
    companion object {
        // 主线程
        const val MAIN = 1
        // 同步，当前线程
        const val SYNC = 2
        // 异步，子线程
        const val ASYNC = 3
    }
}
