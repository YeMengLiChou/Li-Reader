package cn.li.reader.core.entity

/**
 *
 *
 * @author Grimrise 2024/9/24
 */
data class Spine(
    val resources: List<SpineItem>,
    val tocIdref: String? = null,
    val pageProgressionDirection: String? = null,
) {
}