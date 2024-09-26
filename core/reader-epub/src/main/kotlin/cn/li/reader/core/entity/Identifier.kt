package cn.li.reader.core.entity

import cn.li.reader.core.consts.MetaProperty
import cn.li.reader.core.entity.base.AdditionalMeta


/**
 * EPUB 的标识信息，对应 `<dc:identifier>` 数据
 *
 * */
data class Identifier(
    val value: String,
    val id: String? = null,
): AdditionalMeta(id) {

    /**
     * 是否为唯一标识符
     * */
    var unique = false

    override fun toString(): String {
        return "MetadataIdentifier(id=$refinesId, value=${value}', metas=${getAdditionalMeta()})"
    }

    fun getIdentifierType() = get(MetaProperty.IDENTIFIER_TYPE)

}
