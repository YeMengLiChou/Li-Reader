package cn.li.baselib.ext

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.util.Locale

/**
 *
 *
 * @author Grimrise 2024/10/20
 */

val Context.locale: Locale get() {
    // use for >= 17
    return resources.configuration.locales[0]
}


fun Context.color(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.font(@FontRes fontRes: Int): Typeface? {
    return ResourcesCompat.getFont(this, fontRes)
}

fun Context.drawable(@DrawableRes drawableRes: Int): Drawable? {
    return ContextCompat.getDrawable(this, drawableRes)
}

fun Context.string(@StringRes stringRes: Int): String {
    return ContextCompat.getString(this, stringRes)
}
