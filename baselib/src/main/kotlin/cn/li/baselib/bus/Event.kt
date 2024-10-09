package cn.li.baselib.bus


/**
 * 用于区分不同的事件发布，根据 [type] 和 [tag] 进行区分
 *
 * @author Grimrise 2024/9/20
 */
data class Event(
    var clazz: Class<*>,
    var tag: String = DEFAULT_TAG,
) {
    internal var data: Any? = null // sticky

    companion object {
        const val DEFAULT_TAG = "default"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other is Class<*>) return this.clazz === other
        if (other is Event) return this.clazz === other.clazz && this.tag == other.tag
        return false
    }

    override fun hashCode(): Int {
        var result = clazz.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

}
