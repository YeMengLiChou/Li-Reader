package cn.li.reader.core.entity.base


/**
 * `<meta>` 元素带有 `refines` 用于对某个元素的内容进行细化；
 *
 * 此类用于存储这些附加的内容，对于需要保存附加信息的实体，应继承此类
 * */
abstract class AdditionalMeta(
    val refinesId: String?
) {
    
    // property 为 key
    private var metas: HashMap<String, Meta>? = null

    operator fun set(key: String, value: Meta) {
        if (metas == null) metas = HashMap()
        metas!![key] = value
    }

    operator fun get(key: String): Meta? {
        return metas?.get(key)
    }

    fun getAdditionalMeta() = metas

    override fun toString(): String {
        val result = StringBuilder()
        result.append("{")
        if (metas != null) {
            metas?.forEach {
                result.append(it.key).append("=").append(it.value).append(",")
            }
        }
        result.append("}")
        return "AdditionalMeta(id=$refinesId, metas=$result)"
    }

    override fun equals(other: Any?): Boolean {
        return other is AdditionalMeta && other.refinesId == refinesId && other.metas == metas
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + refinesId.hashCode()
        result = 31 * result + metas.hashCode()
        return result
    }
}