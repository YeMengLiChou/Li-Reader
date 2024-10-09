package cn.li.reader.core.entity

import cn.li.reader.core.consts.MetaProperty
import cn.li.reader.core.entity.base.AdditionalMeta


/**
 * 贡献者信息，对应 `<dc:contributor>` 信息
 *
 * */
data class Contributor(
    val name: String,
    val id: String? = null
): AdditionalMeta(id) {

    fun getRole() = get(MetaProperty.ROLE)?.value

}
