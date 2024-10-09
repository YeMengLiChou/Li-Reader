package cn.li.baselib.interfaces

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 *
 *
 * @author Grimrise 2024/9/19
 */
interface IViewBindingProvider<VB: ViewBinding> {

    /**
     * ViewBinding 创建
     * */
    fun createViewBinding(): VB?

    fun createFragmentViewBinding(layoutInflater: LayoutInflater, container: ViewGroup?): VB?
}