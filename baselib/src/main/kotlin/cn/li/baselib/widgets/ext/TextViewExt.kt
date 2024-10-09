package cn.li.baselib.widgets.ext

import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import cn.li.baselib.dimen.dp
import cn.li.reader.R

/**
 * 设置自动适配字体大小，当当前字体大小 [defaultSize] 无法完全显示时，缩小到能够显示，最小值为 [minSize]
 *
 * @author Grimrise 2024/9/19
 */
internal class TextAutoSizeWatcher(
    var defaultSize: Float,
    var minSize: Float,
    var space: Float,
    private val textView: TextView
): TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        val text = s.toString()
        val paint = Paint().apply {
            textSize = defaultSize
        }
        var size = defaultSize
        val maxWidth = textView.width - textView.paddingLeft - textView.paddingRight - space.dp
        while (size > minSize) {
            val textWidth = paint.measureText(text)
            if (textWidth <= maxWidth) {
                break
            } else {
                size -= 1
                paint.textSize = size
            }
        }
        textView.textSize = size
    }
}

/**
 * 自动适配字体大小，当当前字体大小 [defaultSize] 无法完全显示时，缩小到能够显示，最小值为 [minSize]
 * @param defaultSize 当前显示的字体大小 sp units
 * @param minSize 最小显示的字体大小 sp units
 * @param space 额外的空间 dp units
 * @see [unsetAutoTextSize]
 * */
fun TextView.setAutoTextSize(defaultSize: Float, minSize: Float, space: Float = 0f) {
    var textWatcher = getTag(R.id.textview_tag_auto_size) as? TextAutoSizeWatcher
    if (textWatcher == null) {
        textWatcher = TextAutoSizeWatcher(defaultSize, minSize, space, this)
        addTextChangedListener(textWatcher)
        setTag(R.id.textview_tag_auto_size, textWatcher)
    } else {
        textWatcher.defaultSize = defaultSize
        textWatcher.minSize = minSize
    }
}

fun TextView.unsetAutoTextSize() {
    removeTextChangedListener((getTag(R.id.textview_tag_auto_size) as? TextAutoSizeWatcher))
}