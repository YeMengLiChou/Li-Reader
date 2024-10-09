package cn.li.baselib.manager

import android.content.SharedPreferences
import cn.li.baselib.storage.sp.MMKVAutoCloseSharePreference

/**
 *
 *
 * @author Grimrise 2024/9/22
 */
class KvManager private constructor() {


    companion object {
        val inst by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { KvManager() }
    }

    /**
     * 返回一个 [id] 对应的 [SharedPreferences]
     *
     * */
    fun getSp(id: String): SharedPreferences {
        return MMKVAutoCloseSharePreference(id)
    }

}