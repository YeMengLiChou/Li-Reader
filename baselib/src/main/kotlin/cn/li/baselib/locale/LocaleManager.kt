package cn.li.baselib.locale

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import cn.li.baselib.base.BaseApplication
import cn.li.baselib.ext.locale
import cn.li.baselib.manager.BaseActivityManager
import cn.li.baselib.manager.KvManager
import cn.li.baselib.utils.Logger
import java.util.Locale


/**
 *
 * note: 注意 androidx.core:core 版本需要在 1.8 及以上，否则会出现多语言字体加载问题；
 *
 * @author Grimrise 2024/10/17
 */
object LocaleManager {
    private const val TAG = "LocaleManager"
    private const val KEY_APP_LOCALE = "app-locale"

    private val mLocaleChangeListeners = mutableListOf<OnLocaleChangeListener>()

    @Volatile
    private var mInitialized = false

    /***
     * 初始化 Locale，注册 recreate 回调
     *
     * 必须在 [BaseApplication.attachBaseContext] 前完调用
     *
     * @param backMainWhenSwitchLanguage 当切换语言时是否返回主页
     * */
    fun init(backMainWhenSwitchLanguage: Boolean = true) {
        assert(!mInitialized) { "LocaleManager must initialize exactly once!" }
        mInitialized = true

        val recreateListener = OnLocaleChangeListener { _, _, needRecreate ->
            if (!needRecreate) {
                return@OnLocaleChangeListener
            }
            val mainActivity = BaseActivityManager.mainActivity
            if (backMainWhenSwitchLanguage) {
                mainActivity.recreate()
                BaseActivityManager.gotoMainActivity()
            } else {
                BaseActivityManager.forEachActivity { it.recreate() }
            }
        }
        registerLocaleChangeListener(recreateListener)

        // apply locale from sp
        val localeFromSp = KvManager.defaultSp.getString(KEY_APP_LOCALE, null)
        if (localeFromSp != null) {
            val locale = Locale(localeFromSp)
            setAppLocale(null, locale, false)
        }
    }

    fun registerLocaleChangeListener(listener: OnLocaleChangeListener) {
        if (listener !in mLocaleChangeListeners) {
            mLocaleChangeListeners.add(listener)
        }
    }

    fun unregisterLocaleChangeListener(listener: OnLocaleChangeListener) {
        mLocaleChangeListeners.remove(listener)
    }

    fun createConfigurationContext(context: Context, configuration: Configuration): Context? {
        return context.createConfigurationContext(configuration)
    }

    fun getAppLocale(): Locale {
        // after 17
        return BaseApplication.getApp().locale
    }

    fun switchLanguage(locale: Locale) {
        val curLocale = getAppLocale()
        if (locale == curLocale) {
            Logger.info(TAG, "switchLanguage: curLocale $curLocale is same as  $locale so do nothing.")
            return
        }
        setAppLocale(curLocale, locale, true)
    }

    /**
     * 将 locale 应用到 [context]，返回一个新的 context
     * */
    fun applyLocaleConfigurationFor(context: Context): Context {
        val appLocale = getAppLocale()
        if (appLocale != context.locale) {
            val configuration = context.resources.configuration.apply {
                setLocale(appLocale)
            }
            return context.createConfigurationContext(configuration)
        }
        return context
    }

    private fun setAppLocale(oldLocale: Locale?, locale: Locale, needRecreate: Boolean) {
        BaseApplication.getApp().resources.configuration.setLocale(locale)
        Locale.setDefault(locale)
        saveAppLocale(locale)
        notifyLocaleChanged(oldLocale = oldLocale, newLocale =  locale, needRecreate = needRecreate)
    }

    private fun saveAppLocale(locale: Locale) {
        KvManager.defaultSp.edit {
            putString(KEY_APP_LOCALE, locale.toLanguageTag())
        }
    }

    private fun notifyLocaleChanged(oldLocale: Locale?, newLocale: Locale, needRecreate: Boolean) {
        for (listener in mLocaleChangeListeners) {
            listener.onLocaleChange(oldLocale, newLocale, needRecreate)
        }
    }

    fun interface OnLocaleChangeListener {
        /**
         * 语言切换回调方法
         * @param oldLocale
         * @param newLocale
         * @param needRecreate 是否为重建
         * */
        fun onLocaleChange(oldLocale: Locale?, newLocale: Locale, needRecreate: Boolean)
    }
}