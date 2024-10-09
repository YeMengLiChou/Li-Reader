package cn.li.reader.core.entity

import cn.li.reader.core.consts.MetaProperty
import cn.li.reader.core.entity.base.AdditionalMeta


/**
 * 作者信息，对应 `<dc:creator>`
 *
 * @author Grimrise 2024/9/24
 */
data class Author(
    val name: String,
    val id: String? = null,
): AdditionalMeta(id) {

    fun getRole() = get(MetaProperty.ROLE)?.value

    fun getNormalizedName() = get(MetaProperty.FILE_AS)?.value

    fun getAlternateName() = get(MetaProperty.ALTERNATE_SCRIPT)?.value

}