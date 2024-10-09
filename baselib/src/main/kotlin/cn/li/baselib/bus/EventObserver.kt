package cn.li.baselib.bus

import cn.li.baselib.utils.Logger
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Collections
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * 事件分发，允许不同模块下以解耦合的方式进行通信；
 *
 * 注意点：
 * 1. 父类被 @Subscriber 修饰的方法也会纳入接收位置
 * 2. 不能区分泛型，比如 Pair<Int, Int> 和 Pair<String, String> 会视为同一个
 *
 * @author Grimrise 2024/9/19
 */
class EventObserver private constructor() {

    companion object {
        const val TAG = "EventObserver"

        @JvmStatic
        val inst by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { EventObserver() }
    }

    private val mLock = ReentrantLock()

    private val mEventSubscriberRouter = EventSubscriberMap()
        .also {
            EventDispatcher.inst.setEventSubscriberMap(it)
        }

    private val mStickyEvents = Collections.synchronizedList(mutableListOf<Event>())

    private var isDebug = false

    fun setDebug(isDebug: Boolean) {
        this.isDebug = isDebug
    }

    /**
     * 注册事件监听，查找 [subscriber] 中被 [Subscriber] 修饰的方法并注册；
     *
     * @see unsubscribe 请记得取消注册，以防内存泄露
     * */
    fun subscribe(subscriber: Any, isSticky: Boolean = false) {
        try {
            mLock.withLock {
                var clazz: Class<*>? = subscriber::class.java
                // 遍历父类是否存在对应的 @Subscriber 方法
                while (clazz != null && !checkSystemClass(clazz)) {
                    clazz.declaredMethods.forEach { method ->
                        method.isAccessible = true
                        val annotation = method.getAnnotation(Subscriber::class.java)
                        if (annotation != null) {
                            addSubscribeMethod(method, subscriber, annotation.eventTag, annotation.threadMode)
                        }
                    }
                    clazz = clazz.superclass
                }
                if (isSticky) {
                    handleStickyEvents(subscriber)
                }
            }
        } catch (e: Exception) {
            Logger.error(TAG, "subscribe failed", e)
            if (isDebug) {
                throw e
            }
        }
    }


    /**
     * 提供 Kotlin dsl 方式注册事件监听，该种方法需要拿到 [subscriber] 引用才能 [unsubscribe]
     * @param eventClass 需要监听的事件对象
     * @param tag 事件标签
     * @param threadMode 事件分发所在线程
     * @param subscriber 事件监听回调
     * @return 返回 [subscriber] 对象，以便取消注册
     * */
    fun <T> subscribe(
        eventClass: Class<T>,
        tag: String = Event.DEFAULT_TAG,
        @ThreadMode threadMode: Int = ThreadMode.MAIN,
        isSticky: Boolean = false,
        subscriber: (event: T) -> Unit
    ): Any {
        try {
            mLock.withLock {
                val invokeMethod = subscriber::class.java.getMethod("invoke", eventClass).apply {
                    isAccessible = true
                }
                addSubscribeMethod(invokeMethod, subscriber, tag, threadMode)
                if (isSticky) {
                    handleStickyEvents(subscriber)
                }
            }
        } catch (e: Exception) {
            Logger.error(TAG, "subscribe failed", e)
            if (isDebug) {
                throw e
            }
        }
        return subscriber
    }

    /**
     * 注册事件接受方法
     * */
    private fun addSubscribeMethod(
        method: Method,
        subscriber: Any,
        eventTag: String,
        @ThreadMode threadMode: Int
    ) {
        if (method.modifiers and (Modifier.ABSTRACT) != 0) {
            throw IllegalStateException("The method `${subscriber::class.java.name}#${method.name}`" +
                    " annotated with `@EventSubscriber` must not be abstract!")
        }

        val parameters: Array<Class<*>>? = method.parameterTypes
        // 只有一个参数的方法才能注册
        if (parameters != null && parameters.size == 1) {
            val event = Event(clazz = convertParameterType(parameters.first()), tag = eventTag)
            val eventSubscriber = EventSubscriber(subscriber, event, method, threadMode)
            mEventSubscriberRouter.addEventSubscriber(eventSubscriber)
        } else {
            throw IllegalArgumentException("The method `${subscriber::class.java.name}#${method.name}` " +
                    "annotated with `@EventSubscriber` must have exactly one parameter!")
        }
    }

    /**
     * 排除系统类
     * @return 如果是系统类返回 true
     * */
    private fun checkSystemClass(clazz: Class<*>): Boolean {
        return clazz === Any::class.java
                || clazz === Object::class.java
                || clazz.name.startsWith("java")
                || clazz.name.startsWith("javax")
                || clazz.name.startsWith("android")
                || clazz.name.startsWith("androidx")
    }

    /**
     * 因为最终编译成 Jvm 字节码，所以 Kotlin 的数值类型会变为 Java 的数值类型，同时函数传字面量时也是 Java 的数值类型
     * */
    private fun convertParameterType(clazz: Class<*>): Class<*> {
        if (clazz == Byte::class.java) return java.lang.Byte::class.java
        if (clazz == Short::class.java) return java.lang.Short::class.java
        if (clazz == Int::class.java) return java.lang.Integer::class.java
        if (clazz == Boolean::class.java) return java.lang.Boolean::class.java
        if (clazz == Long::class.java) return java.lang.Long::class.java
        if (clazz == Float::class.java) return java.lang.Float::class.java
        if (clazz == Double::class.java) return java.lang.Double::class.java
        return clazz
    }


    /**
     * 取消事件的监听
     * @param subscriber 取消订阅的对象
     * @see subscribe
     * */
    fun unsubscribe(subscriber: Any) {
        try {
            mLock.withLock {
                mEventSubscriberRouter.removeEventSubscriber(subscriber)
            }
        } catch (e: Exception) {
            Logger.error(TAG, "unsubscribe failed", e)
            if (isDebug) {
                throw e
            }
        }
    }


    /**
     * 分发事件实体 [data]，以 [tag] 加以区分
     * @param data 分发事件实体
     * @param tag 事件标识
     * */
    fun publish(data: Any, tag: String = Event.DEFAULT_TAG, isSticky: Boolean = false) {
        val event = Event(convertParameterType(data::class.java), tag)
        if (isSticky) {
            event.data = data
            mStickyEvents.add(event)
        } else {
            EventDispatcher.inst.dispatch(data, tag)
        }
    }


    private fun handleStickyEvents(subscriber: Any) {
        mStickyEvents.forEach { event ->
            EventDispatcher.inst.dispatchSticky(event, subscriber)
        }
    }
}
