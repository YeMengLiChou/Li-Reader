package cn.li.baselib.base

import android.app.Application
import android.content.Context
import cn.li.baselib.manager.BaseActivityManager

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
        fun getApp(): Context = sApplication

        @JvmStatic
        fun isApplicationReady(): Boolean = sApplicationReady
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        sApplication = this
        sApplicationReady = true
        BaseActivityManager.register(this)
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}