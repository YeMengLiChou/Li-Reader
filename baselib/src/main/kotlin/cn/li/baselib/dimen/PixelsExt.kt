package cn.li.baselib.dimen

import android.util.TypedValue
import cn.li.baselib.base.BaseApplication


/**
 * dp 2 px
 * */
val Float.dp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, BaseApplication.getApp().resources.displayMetrics)

/**
 * dp 2 px
 * */
val Int.dp: Int
    get() = this.toFloat().dp.toInt()

/**
 * sp 2 px
 * */
val Float.sp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, BaseApplication.getApp().resources.displayMetrics)

/**
 * sp 2 px
 * */
val Int.sp: Int
    get() = this.toFloat().sp.toInt()


val Int.px2dp: Int
    get() = (this / BaseApplication.getApp().resources.displayMetrics.density).toInt()

val Float.px2dp: Float
    get() = this / BaseApplication.getApp().resources.displayMetrics.density

