package cn.li.reader.core.entity

import cn.li.reader.core.entity.base.EpubResource

data class SpineItem(
    val idref: String,
    val linear: String,
    val properties: String? = null,
    val resource: EpubResource

)
