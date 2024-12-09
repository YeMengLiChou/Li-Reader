package cn.li.app.test

import cn.li.startup.common.annotation.Startup
import cn.li.startup.common.constants.StartupStage

/**
 *
 *
 * @author Grimrise 2024/10/10
 */
@Startup(
    key = "cn/li/app/test",
    name = "cn/li/app/test",
    desc = "cn/li/app/test",
    dependOn = [],
    startAt = StartupStage.SPLASH_CREATE_START_2_SUPER,
    lastAt = StartupStage.SPLASH_CREATE_START_2_SUPER,
    runInMain = false,
)
class TestStartTask {
}