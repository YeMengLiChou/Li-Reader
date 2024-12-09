package cn.li.startup.common.constants

/**
 *
 *
 * @author Grimrise 2024/10/10
 */
enum class StartupStage(
    val value: Int,
) {

    APPLICATION_ATTACH_START_2_SUPER(0),
    APPLICATION_ATTACH_SUPER_2_END(1),

    APPLICATION_CREATE_START_2_SUPER(2),
    APPLICATION_CREATE_SUPER_2_END(3),

    SPLASH_CREATE_START_2_SUPER(4),
    SPLASH_CREATE_SUPER_2_END(5),

    SPLASH_RESUME_START_2_SUPER(6),
    SPLASH_RESUME_SUPER_2_END(7),

    MAIN_CREATE_START_2_SUPER(8),
    MAIN_CREATE_SUPER_2_END(9),

    MAIN_RESUME_START_2_SUPER(10),
    MAIN_RESUME_SUPER_2_END(11),

    NONE(12)

}