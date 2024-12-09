package cn.li.baselib.ext

import android.view.View
import android.view.ViewGroup.MarginLayoutParams

/**
 *
 *
 * @author Grimrise 2024/10/20
 */
private const val DEFAULT_MARGIN = -1

/**
 * 设置 View 的 margin，单位 px
 * */
fun View.setMargin(
    left: Int = DEFAULT_MARGIN,
    top: Int = DEFAULT_MARGIN,
    right: Int = DEFAULT_MARGIN,
    bottom: Int = DEFAULT_MARGIN
) {

    val layoutParams = layoutParams as? MarginLayoutParams ?: return
    layoutParams.leftMargin = if (left != DEFAULT_MARGIN) left else layoutParams.leftMargin
    layoutParams.topMargin = if (top != DEFAULT_MARGIN) top else layoutParams.topMargin
    layoutParams.rightMargin = if (right != DEFAULT_MARGIN) right else layoutParams.rightMargin
    layoutParams.bottomMargin = if (bottom != DEFAULT_MARGIN) bottom else layoutParams.bottomMargin
}

/**
 * 设置 View 的 margin，单位 px
 * */
fun View.setMargin(
    margin: Int
) {
    setMargin(margin, margin, margin, margin)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}