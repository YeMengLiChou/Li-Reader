package cn.li.baselib.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import cn.li.baselib.interfaces.IViewBindingProvider

/**
 *
 *
 * @author Grimrise 2024/9/19
 */
abstract class BaseActivity<VB: ViewBinding>:
    AppCompatActivity(),
    IViewBindingProvider<VB>
{

    protected val mBinding: VB by lazy {
        createViewBinding()!!
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        bindListeners()

        setContentView(mBinding.root)
    }

    /**
     * 初始化 View 控件
     * */
    abstract fun initViews()

    /**
     * 设置相关的观察、订阅、监听器
     * */
    open fun bindListeners() {}

    final override fun createFragmentViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): VB? = null

}