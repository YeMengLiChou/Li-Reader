package cn.li.baselib.storage.sp

import android.content.SharedPreferences
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import cn.li.baselib.base.BaseApplication
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import java.util.Collections
import java.util.HashMap

/**
 * 自动关闭的 MMKV 的 SharedPreference，避免多个 MMKV 实例造成 fd 泄露
 *
 *
 * @author Grimrise 2024/9/22
 */
internal class MMKVAutoCloseSharePreference(
    private val cacheId: String
): SharedPreferences, SharedPreferences.Editor {

    internal data class MMKVSPCache(
        val mmkv: MMKV,
        var lastUpdateTime: Long = 0L,
    )

    companion object {
        private const val TAG = "MMKVSharePreference"

        // 全局唯一的 cache
        private val mmkvCache = Collections.synchronizedMap(HashMap<String, MMKVSPCache>())

        // 检测自动关闭的间隔
        private const val DETECT_INTERVAL = 3L * 1000

        // cache 自动关闭的最大时间
        private const val AUTO_CLOSE_LIMIT = 15L * 1000

        // 子线程检测
        private val autoCloseHandler = Handler(HandlerThread(TAG).also { it.start() }.looper)

        // 自动关闭
        private val autoCloseRunnable = object : Runnable {
            override fun run() {
                if (mmkvCache.isEmpty()) return
                synchronized(mmkvCache) {
                    val iterator = mmkvCache.iterator()
                    while (iterator.hasNext()) {
                        val entry = iterator.next()
                        // 删除指定时间没有使用过的 cache
                        if (SystemClock.elapsedRealtime() - entry.value.lastUpdateTime > AUTO_CLOSE_LIMIT) {
                            entry.value.mmkv.close()
                            iterator.remove()
                        }
                    }
                }
                autoCloseHandler.postDelayed(this, DETECT_INTERVAL)
            }
        }

        // 开始检测
        init {
            autoCloseHandler.postDelayed(autoCloseRunnable, DETECT_INTERVAL)
        }

        private fun newMMKVInstance(cacheId: String): MMKV {
            synchronized(this::class) {
                val mmkv = try {
                    MMKV.mmkvWithID(cacheId, MMKV.MULTI_PROCESS_MODE)
                } catch (e: Throwable) {
                    MMKV.initialize(BaseApplication.getContext())
                    MMKV.setLogLevel(MMKVLogLevel.LevelWarning)
                    MMKV.mmkvWithID(cacheId, MMKV.MULTI_PROCESS_MODE)
                }
                return mmkv
            }
        }
    }


    private fun getMMKVCache(): MMKV {
        var cache = mmkvCache[cacheId]
        if (cache == null) {
            cache = MMKVSPCache(mmkv = newMMKVInstance(cacheId)).also {
                mmkvCache[cacheId] = it
            }
        }
        // 更新不同的事件
        cache.lastUpdateTime = SystemClock.elapsedRealtime()
        return cache.mmkv
    }


    override fun getAll(): MutableMap<String, *> {
        return getMMKVCache().all
    }

    override fun getString(key: String?, defValue: String?): String? {
        return getMMKVCache().decodeString(key, defValue)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        return getMMKVCache().decodeStringSet(key, defValues)
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return getMMKVCache().decodeInt(key, defValue)
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return getMMKVCache().decodeLong(key, defValue)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return getMMKVCache().decodeFloat(key, defValue)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return getMMKVCache().decodeBool(key, defValue)
    }

    override fun contains(key: String?): Boolean {
        return getMMKVCache().containsKey(key)
    }

    override fun edit(): SharedPreferences.Editor {
        return getMMKVCache().edit()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        return getMMKVCache().registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        return getMMKVCache().unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun putString(key: String?, value: String?): SharedPreferences.Editor {
        return getMMKVCache().edit().putString(key, value)
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
        return getMMKVCache().edit().putStringSet(key, values)
    }

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
        return getMMKVCache().edit().putInt(key, value)
    }

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
        return getMMKVCache().edit().putLong(key, value)
    }

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
        return getMMKVCache().edit().putFloat(key, value)
    }

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
        return getMMKVCache().edit().putBoolean(key, value)
    }

    override fun remove(key: String?): SharedPreferences.Editor {
        return getMMKVCache().edit().remove(key)
    }

    override fun clear(): SharedPreferences.Editor {
        return getMMKVCache().edit().clear()
    }

    override fun commit(): Boolean {
        return getMMKVCache().edit().commit()
    }

    override fun apply() {
        return getMMKVCache().edit().apply()
    }
}