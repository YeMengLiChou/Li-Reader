package cn.li.baselib.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import cn.li.baselib.interfaces.IViewBindingProvider

/**
 *
 *
 * @author Grimrise 2024/9/19
 */
abstract class BaseFragment<VB: ViewBinding>: Fragment(), IViewBindingProvider<VB> {

    private lateinit var mBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onContentCreate(inflater, container, savedInstanceState)
        return createFragmentViewBinding(inflater, container)!!
            .also { mBinding = it }
            .root
    }

    abstract fun onContentCreate(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )

    abstract fun onVisible()

    abstract fun onInvisible()

    final override fun createViewBinding(): VB? = null

}