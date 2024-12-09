package cn.li.baselib.manager

import android.content.SharedPreferences
import cn.li.baselib.storage.sp.MMKVAutoCloseSharePreference

/**
 *
 *
 * @author Grimrise 2024/9/22
 */
object KvManager {

    const val TAG = "KvManager"

    private const val DEFAULT_SP_ID = "default-kv"

    /**
     * 返回一个 [id] 对应的 [SharedPreferences]
     *
     * */
    fun obtainSp(id: String): SharedPreferences {
        return MMKVAutoCloseSharePreference(id)
    }

    val defaultSp by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        obtainSp(DEFAULT_SP_ID)
    }
}