package cn.li.app.pages

import cn.li.app.databinding.ActivityMainBinding
import cn.li.baselib.base.BaseActivity
import cn.li.baselib.bus.Subscriber
import cn.li.baselib.bus.ThreadMode
import cn.li.baselib.manager.annotation.ActivityMain

@ActivityMain
class MainActivity: BaseActivity<ActivityMainBinding>() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun initViews() {
    }

    override fun createViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    @Subscriber(threadMode = ThreadMode.ASYNC)
    fun onEvent(event: Pair<String, Int>) {
//        Logger.info("MainActivity", "onEvent: $event, isMainThread: ${isMainThread()}")
//        ToastUtils.show("onEvent: $event", Toast.LENGTH_SHORT)
    }

    @Subscriber(threadMode = ThreadMode.ASYNC)
    fun onEvent2(event: Pair<String, Int>) {
//        Logger.info("MainActivity", "onEvent2: $event, isMainThread: ${isMainThread()}")
//        ToastUtils.show("onEvent2: $event", Toast.LENGTH_SHORT)
    }

}