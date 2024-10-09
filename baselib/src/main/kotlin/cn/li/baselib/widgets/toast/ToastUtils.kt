package cn.li.baselib.widgets.toast

import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import cn.li.baselib.base.BaseApplication
import java.lang.ref.WeakReference

/**
 *
 *
 * @author Grimrise 2024/9/27
 */
class ToastUtils {

    @IntDef(value= [Toast.LENGTH_SHORT, Toast.LENGTH_LONG])
    @Retention(AnnotationRetention.SOURCE)
    private annotation class Duration

    companion object {
        private var sToast: WeakReference<Toast>? = null


        fun show(text: CharSequence, @Duration duration: Int = Toast.LENGTH_SHORT) {
            sToast?.get()?.cancel()
            sToast = WeakReference(Toast.makeText(BaseApplication.getApp(), text, duration))
            sToast?.get()!!.show()
        }

        fun show(@StringRes resId: Int, @Duration duration: Int = Toast.LENGTH_SHORT) {
            sToast?.get()?.cancel()
            sToast = WeakReference(Toast.makeText(BaseApplication.getApp(), resId, duration))
            sToast?.get()!!.show()
        }
    }
}