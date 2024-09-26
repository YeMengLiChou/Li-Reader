package cn.li.reader.core.entity.base

/**
 * 对应 `<meta>` 信息
 *
 * */
data class Meta(
    val property: String,
    val value: String,
    val refines: String?,
    val scheme: String?,
    val id: String?
)
