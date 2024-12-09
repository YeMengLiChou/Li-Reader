package cn.li.baselib.drawable

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation
import androidx.annotation.ColorInt

/**
 *
 *
 * @author Grimrise 2024/10/12
 */

fun buildGradientDrawable(
    orientation: Orientation = Orientation.TOP_BOTTOM,
    @ColorInt
    colors: IntArray? = null,
    config: GradientDrawable.() -> Unit
): GradientDrawable {
    val drawable = GradientDrawable(orientation, colors)
    drawable.config()
    return drawable
}


fun GradientDrawable.radius(leftTop: Float = 0f, rightTop: Float = 0f, rightBottom: Float = 0f, leftBottom: Float = 0f) {
    cornerRadii = floatArrayOf(leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom)
}

fun GradientDrawable.rectangle() {
    shape = GradientDrawable.RECTANGLE
}

fun GradientDrawable.oval() {
    shape = GradientDrawable.OVAL
}

fun GradientDrawable.line() {
    shape = GradientDrawable.LINE
}

fun GradientDrawable.ring() {
    shape = GradientDrawable.RING
}

fun GradientDrawable.stroke(width: Int, @ColorInt color: Int, dashWidth: Float = 0f, dashGap: Float = 0f) {
    setStroke(width, color, dashWidth, dashGap)
}

fun GradientDrawable.stroke(width: Int, colorStateList: ColorStateList, dashWidth: Float = 0f, dashGap: Float = 0f) {
    setStroke(width, colorStateList, dashWidth, dashGap)
}



fun main() {
    buildGradientDrawable {
    }
}