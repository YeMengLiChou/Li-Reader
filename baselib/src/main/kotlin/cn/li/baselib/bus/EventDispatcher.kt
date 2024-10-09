package cn.li.baselib.bus

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.util.concurrent.ConcurrentHashMap

/**
 *  事件分发
 *
 * @author Grimrise 2024/9/20
 */
internal class EventDispatcher private constructor() {

    companion object {
        class MainEventDispatcher: Handler(Looper.getMainLooper())

        @JvmStatic
         val inst by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            EventDispatcher()
        }
    }

    // 主线程
    private val mMainEventDispatcher = MainEventDispatcher()
    // 异步子线程
    private val mAsyncEventThread = HandlerThread("BusEventAsyncSender")
        .also {
            it.start()
        }
    // 拿到子线程的 Looper
    private val mAsyncEventDispatcher = Handler(mAsyncEventThread.looper)

    // 一个 Event 以及其父类+接口类
    private val mEventCache = ConcurrentHashMap<Event, MutableList<Event>>()

    private lateinit var mEventSubscriberMap: EventSubscriberMap

    fun setEventSubscriberMap(map: EventSubscriberMap) {
        mEventSubscriberMap = map
    }


    /**
     * 分发事件
     * */
    fun dispatch(data: Any, tag: String) {
        val events = findMatchEvents(data, tag)
        for (event in events) {
            // 找到事件对应的订阅者
            val subscribers = mEventSubscriberMap.getEventSubscribers(event)
            subscribers?.forEach {
                dispatchReal(data, it)
            }
        }
    }

    /**
     * 分发 Sticky 事件
     * */
    fun dispatchSticky(event: Event, subscriber: Any) {
        val events = findMatchEvents(event.data!!, event.tag)
        for (subEvent in events) {
            // 找到事件对应的订阅者
            val subscribers = mEventSubscriberMap.getEventSubscribers(subEvent)
            val eventSubscriber = subscribers?.find {
                it.subscriber.get() == subscriber && it.event == event
            }
            eventSubscriber?.let {
                dispatchReal(event.data!!, it)
            }
        }
    }

    /**
     * 匹配相同类型的 Event，包括父类、接口类；
     * */
    private fun findMatchEvents(data: Any, tag: String): List<Event> {
        val event = Event(data::class.java, tag)
        var result = mEventCache[event]
        if (result == null) {
            val events = mutableListOf<Event>()
            var clazz = data::class.java
            while (clazz != Any::class.java) {
                // 将父类添加
                events.add(Event(data::class.java, tag))
                // 将接口添加进去
                clazz.interfaces.forEach { interfaceClass ->
                    // 避免反复添加
                    if (events.find { it.clazz == interfaceClass } == null) {
                        events.add(Event(interfaceClass, tag))
                    }
                }
                clazz = clazz.superclass
            }
            mEventCache[event] = events
            result = events
        }
        return result
    }

    /**
     * 分发事件到对应的监听对象
     * */
    private fun dispatchReal(data: Any, subscriber: EventSubscriber) {
        when (subscriber.threadMode) {
            // 主线程
            ThreadMode.MAIN -> {
                mMainEventDispatcher.post {
                    subscriber(data)
                }
            }
            // 同步，当前线程执行
            ThreadMode.SYNC -> {
                subscriber(data)
            }
            // 异步，在子线程中执行
            ThreadMode.ASYNC -> {
                mAsyncEventDispatcher.post {
                    subscriber(data)
                }
            }
        }
    }

}


