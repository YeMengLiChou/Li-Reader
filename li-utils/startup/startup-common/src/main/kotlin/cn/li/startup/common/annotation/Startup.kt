package cn.li.startup.common.annotation

import cn.li.startup.common.constants.StartupConstants
import cn.li.startup.common.constants.StartupStage


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Startup(
    val key: String,
    val name: String = "",
    val desc: String = "",
    val priority: Int = StartupConstants.PRIORITY_NORMAL,
    val dependOn: Array<String> = [],
    val startAt: StartupStage,
    val lastAt: StartupStage,
    val runInMain: Boolean = false,
    val process: String = StartupConstants.PROCESS_ALL,
)
