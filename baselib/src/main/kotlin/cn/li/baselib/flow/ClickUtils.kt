package cn.li.baselib.flow

import android.view.View
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 *
 *
 * @author Grimrise 2024/9/20
 */


object ClickUtils {


    fun debounce(vararg views: View, onClick: (view: View) -> Unit) {
        val flow = MutableSharedFlow<View>()
        for (view in views) {
            view.setOnClickListener {
                flow.tryEmit(it)
            }
        }


    }

}