package cn.li.baselib.utils

import android.graphics.Typeface
import java.lang.reflect.Type

/**
 *
 *
 * @author Grimrise 2024/10/17
 */
object TypefaceUtils {

    private val nativeInstanceField by lazy(LazyThreadSafetyMode.PUBLICATION) {
        Typeface::class.java.getField("native_instance")
    }

    val Typeface.nativeInstance: Long get() {
        return nativeInstanceField.get(this) as Long
    }
}