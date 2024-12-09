package cn.li.baselib.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import cn.li.baselib.manager.annotation.ActivityMain
import java.util.Collections


/***
 * 记录 Activity 栈
 *
 * @author Grimrise 2024/09/26
 * */
object BaseActivityManager {

    // 用于缓存 MainActivity 的 Class 对象，避免每次都去反射获取
    private var hasMainActivityAnnoClazz: Class<*>? = null

    private val mActivityStack = Collections.synchronizedList(mutableListOf<Activity>())

    private val mVisibleActivityStack = Collections.synchronizedList(mutableListOf<Activity>())

    private val applicationActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            mActivityStack.add(activity)
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            mVisibleActivityStack.add(activity)
        }

        override fun onActivityPaused(activity: Activity) {
            mVisibleActivityStack.remove(activity)
        }

        override fun onActivityStopped(activity: Activity) {
        }


        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            mActivityStack.remove(activity)
        }
    }

    /**
     * 主 Activity，请用 [ActivityMain] 标注对应的Activity
     * */
    val mainActivity: Activity
        get() = mActivityStack.filter {
                    if (hasMainActivityAnnoClazz != null) {
                        it::class.java == hasMainActivityAnnoClazz
                    } else {
                        it::class.java.getAnnotation(ActivityMain::class.java) != null
                    }
                }.let {
                    if (it.isEmpty())
                        throw IllegalStateException("Make sure that your MAIN activity annotated with `@BaseActivityManager.MainActivity`!")
                    if (it.size > 1)
                        throw IllegalStateException("`@BaseActivityManager.MainActivity` must be exactly annotated with one activity!" +
                                " But there are more than one activities: $it")

            it.first().also { main ->
                        if (hasMainActivityAnnoClazz == null) hasMainActivityAnnoClazz = main::class.java
                    }
                }

    /**
     * 当前栈顶 Activity
     * */
    val currentActivity
        get() = mActivityStack.lastOrNull()

    /**
     * [currentActivity] 的前一个 activity
     * */
    val previousActivity
        get() = getPreviousActivity(1)

    fun getPreviousActivity(index: Int = 1): Activity? =
        if (mActivityStack.size - 1 - index < 0) null else {
            mActivityStack[mActivityStack.size - 1 - index]
        }

    val currentVisibleActivity
        get() = mVisibleActivityStack.lastOrNull()

    val previousVisibleActivity
        get() = mVisibleActivityStack

    fun getPreviousVisibleActivity(index: Int = 1): Activity? =
        if (mVisibleActivityStack.size - 1 - index < 0) null else {
            mVisibleActivityStack[mVisibleActivityStack.size - 1 - index]
        }

    fun remove(activity: Activity) {
        mActivityStack.remove(activity)
    }

    fun removeVisible(activity: Activity) {
        mVisibleActivityStack.remove(activity)
    }

    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(applicationActivityLifecycleCallbacks)
    }


    /**
     * 退出应用
     * */
    fun exitApp() {
        for (activity in mActivityStack) {
            activity.finish()
        }
    }

    /**
     * 回到主 Activity，并清空栈所在位置上的所有 activity
     *
     * */
    fun gotoMainActivity() {
        val iterator = mActivityStack.iterator()
        val main = mainActivity
        var findMain = false
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (activity === main) {
                findMain = true
            } else if (findMain) {
                activity.finish()
                iterator.remove()
            }
        }
    }

    fun forEachActivity(action: (Activity) -> Unit) {
        mActivityStack.forEach(action)
    }
}