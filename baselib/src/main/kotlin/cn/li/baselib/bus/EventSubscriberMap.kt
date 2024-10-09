package cn.li.baselib.bus

import cn.li.baselib.utils.Logger
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

/**
 * 存放订阅事件类型之间的关系
 *
 * @author Grimrise 2024/9/26
 */
internal class EventSubscriberMap {

    companion object {
        const val TAG = "EventSubscriberMap"
    }

    // 根据事件找 Subscriber
    private val mEvent2EventSubscribers = ConcurrentHashMap<Event, HashSet<EventSubscriber>>()

    // 根据订阅者找 Subscriber
    private val mSubscribers2EventSubscribers = Collections.synchronizedMap(WeakHashMap<Any, HashSet<EventSubscriber>>())

    /**
     * 添加
     * */
    fun addEventSubscriber(subscriber: EventSubscriber) {
        val event = subscriber.event
        // 添加事件对应的 Subscriber
        var set = mEvent2EventSubscribers[event]
        if (set == null) {
            set = HashSet()
            mEvent2EventSubscribers[event] = set
        }
        set.add(subscriber)

        // 反向添加，方便移除
        val target = subscriber.subscriber.get()!!
        var events = mSubscribers2EventSubscribers[target]
        if (events == null) {
            events = HashSet()
            mSubscribers2EventSubscribers[target] = events
        }
        events.add(subscriber)
    }

    fun removeEventSubscriber(subscriber: Any) {
        val eventSubscribers = mSubscribers2EventSubscribers.remove(subscriber)
        Logger.info(TAG, "remove: $eventSubscribers")
        eventSubscribers?.forEach { es ->
            mEvent2EventSubscribers[es.event]?.remove(es)
        }
        eventSubscribers?.clear()
    }


    fun getEventSubscribers(event: Event): List<EventSubscriber>? {
        return mEvent2EventSubscribers[event]?.toList()
    }

}