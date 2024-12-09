package cn.li.app.app

import cn.li.baselib.base.BaseApplication
import cn.li.baselib.bus.EventObserver

/**
 *
 *
 * @author Grimrise 2024/9/19
 */
class LiReaderApp: BaseApplication() {

    override fun onCreate() {
        super.onCreate()

        EventObserver.inst.publish(Pair(1,2), isSticky = true)
    }
}