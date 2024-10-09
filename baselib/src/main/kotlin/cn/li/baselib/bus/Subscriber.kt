package cn.li.baselib.bus

/**
 * 用于声明方法为指定事件的订阅者。
 * - 会获取父类中该注解修饰的方法
 * - 不支持修饰接口中的方法，当实现方法时需要手动标识
 *
 * @param eventTag 标签，用于区分同一参数不同事件
 * @param threadMode 线程模式，默认为主线程 [ThreadMode.MAIN]
 * @author Grimrise on 2024/09/19
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscriber(
    val eventTag: String = Event.DEFAULT_TAG,
    @ThreadMode
    val threadMode: Int = ThreadMode.MAIN,
)
