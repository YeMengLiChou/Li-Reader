package cn.li.baselib.bus

import java.lang.ref.WeakReference
import java.lang.reflect.Method

/**
 * 事件监听对象封装
 *
 * @author Grimrise 2024/9/20
 */
class EventSubscriber(
    subscriber: Any,
    val event: Event,
    val method: Method,
    @ThreadMode val threadMode: Int
) {

    val subscriber: WeakReference<Any> = WeakReference(subscriber)

    val alive: Boolean get() = subscriber.get() != null

    operator fun invoke(event: Any) {
        subscriber.get()?.let {
            method.invoke(it, event)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is EventSubscriber) return false
        if (event != other.event) return false
        if (threadMode != other.threadMode) return false
        if (subscriber.get() !== other.subscriber.get()) return false
        if (method !== other.method) return false
        return true
    }

    override fun hashCode(): Int {
        var result = threadMode.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + subscriber.hashCode()
        return result
    }

    override fun toString(): String {
        return "EventSubscriberTarget(event=$event method=$method, subscriber=${subscriber.get()}, threadMode=$threadMode)"
    }
}