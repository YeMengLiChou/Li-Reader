package cn.li.baselib.ext

import android.util.TypedValue
import cn.li.baselib.base.BaseApplication


/**
 * dp 2 px
 * */
val Float.dp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, BaseApplication.getApp().resources.displayMetrics)


val Float.dpInt: Int
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, BaseApplication.getApp().resources.displayMetrics).toInt()

val Int.dp: Float
    get() = toFloat().dp

/**
 * dp 2 px
 * */
val Int.dpInt: Int
    get() = toFloat().dpInt

/**
 * sp 2 px
 * */
val Float.sp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, BaseApplication.getApp().resources.displayMetrics)

val Float.spInt: Int
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, BaseApplication.getApp().resources.displayMetrics).toInt()

/**
 * sp 2 px
 * */
val Int.sp: Float
    get() = toFloat().sp

val Int.spInt: Int
    get() = toFloat().spInt


val Int.px2dp: Int
    get() = (this / BaseApplication.getApp().resources.displayMetrics.density).toInt()

val Float.px2dp: Float
    get() = this / BaseApplication.getApp().resources.displayMetrics.density

