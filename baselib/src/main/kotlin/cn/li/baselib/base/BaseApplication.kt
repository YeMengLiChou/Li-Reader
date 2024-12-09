package cn.li.baselib.base

import android.app.Application
import android.content.Context
import cn.li.baselib.locale.LocaleManager
import cn.li.baselib.manager.BaseActivityManager
import cn.li.baselib.utils.Logger

/**
 * 应用 [Application] 需要继承的基类
 * - 获取 Context: [getApp]
 *
 * @author Grimrise 2024/9/19
 * */
abstract class BaseApplication: Application() {

    companion object {
        private lateinit var sApplication: BaseApplication
        private var sApplicationReady: Boolean = false

        @JvmStatic
        fun getApp(): BaseApplication = sApplication

        @JvmStatic
        fun getContext(): Context = sApplication

        @JvmStatic
        fun isApplicationReady(): Boolean = sApplicationReady
    }

    override fun attachBaseContext(base: Context?) {
        Logger.info("BaseApplication", "attachBaseContext:$base")
        sApplication = this
        sApplicationReady = true

        // TODO: move to initialization framework
        super.attachBaseContext(base)
        LocaleManager.init()
        BaseActivityManager.register(this)
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}