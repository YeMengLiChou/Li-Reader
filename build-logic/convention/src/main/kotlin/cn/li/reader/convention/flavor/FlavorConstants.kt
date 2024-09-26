package cn.li.reader.convention.flavor

/**
 *
 *
 * @author Grimrise 2024/9/20
 */
object FlavorConstants {

    /**
     *
     * 当前 FlavorDimension:
     * - `debug`:
     *      - `local_test` 带 debug 功能的测试包
     *      - `official` 不带 debug 功能的测试包
     * */
    annotation class FlavorDimension {
        companion object {
            const val APP = "app"
            const val DEBUG = "debug"
        }
    }

    // 修改名称时，需要 rebuild 一下
    annotation class FlavorType {
        companion object {
            const val LOCAL_TEST = "localtest"
            const val OFFICIAL = "official"
        }

    }
}