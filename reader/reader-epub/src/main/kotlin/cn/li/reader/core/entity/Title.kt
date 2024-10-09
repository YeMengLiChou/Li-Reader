package cn.li.reader.core.entity

import cn.li.reader.core.consts.MetaProperty
import cn.li.reader.core.entity.base.AdditionalMeta


// dc:title
data class Title(
    val title: String,
    val id: String? = null,
    val direction: Int = DIR_LTR
): AdditionalMeta(id) {

    companion object {
        const val DIR_LTR = 0
        const val DIR_RTL = 1

        fun getDirection(text: String?) =
            if (text == null || text == "ltr") DIR_LTR else DIR_RTL
    }

    override fun toString(): String {
        return "MetadataTitle(title='$title', id=${id}, metas=${super.toString()})"
    }

    fun getTitleType() = get(MetaProperty.TITLE_TYPE)?.value

    fun getDisplaySeq() = get(MetaProperty.DISPLAY_SEQ)?.value

    fun getGroupPosition() = get(MetaProperty.GROUP_POSITION)?.value

}