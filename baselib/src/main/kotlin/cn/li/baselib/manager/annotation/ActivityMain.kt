package cn.li.baselib.manager.annotation

/**
 * 标识为主 Activity，用于 [cn.li.baselib.manager.BaseActivityManager.mainActivity]
 * */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ActivityMain