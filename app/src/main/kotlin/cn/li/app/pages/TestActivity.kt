package cn.li.app.pages

import androidx.core.text.buildSpannedString
import cn.li.app.databinding.ActivityTestBinding
import cn.li.baselib.base.BaseActivity
import cn.li.baselib.utils.DeviceUtils
import cn.li.baselib.utils.Logger
import java.util.Locale

/**
 *
 *
 * @author Grimrise 2024/10/17
 */
class TestActivity: BaseActivity<ActivityTestBinding>() {


    override fun initViews() {

//            val resources = resources
//            val config = resources.configuration
//            val locale = config.locales.get(0)
//            if (locale == Locale.UK) {
//                config.setLocale(Locale.CHINA)
//            } else {
//                config.setLocale(Locale.UK)
//            }
//            resources.updateConfiguration(config, resources.displayMetrics)
//            recreate()
            Logger.info("TestActivity", "locale: ${resources.configuration.locales.get(0)}")


        mBinding.textview.text = buildSpannedString {
            append("osSdk: ${DeviceUtils.osSdk}\n")
            append("osBrand: ${DeviceUtils.osBrand}\n")
            append("osModel: ${DeviceUtils.osModel}\n")
            append("totalMemory: ${DeviceUtils.totalMemory}\n")
            append("availableRomSize: ${DeviceUtils.availableRomSize}\n")
            append("totalRomSize: ${DeviceUtils.totalRomSize}\n")
            append("statusBarHeight: ${DeviceUtils.getStatusBarHeight(this@TestActivity)}\n")
            append("checkStatusBarVisible: ${DeviceUtils.checkStatusBarVisible(this@TestActivity)}\n")
            append("navigationBarHeight: ${DeviceUtils.getNavigationBarHeight(this@TestActivity)}\n")
            append("screenSize: ${DeviceUtils.getScreenSize(this@TestActivity)}\n")
            append("cpuApi: ${DeviceUtils.cpuApi}")
        }
    }

    override fun createViewBinding(): ActivityTestBinding? {
        return ActivityTestBinding.inflate(layoutInflater)
    }
}