package cn.li.reader.core.entity

/**
 *
 *
 * @author Grimrise 2024/9/25
 */

class NavigationInfo(
    var title: String? = null,
    val order: Int,
    var src: String? = null,
    var parent: NavigationInfo? = null,
    var children: MutableList<NavigationInfo>? = null,
    var hidden: Boolean = false
) {

    fun traversal(depth: Int = 0, action: (NavigationInfo, Int) -> Unit) {
        action(this, depth)
        children?.forEach {
            it.traversal(depth + 1, action)
        }
    }

    fun toList(): List<NavigationInfo> {
        val list = mutableListOf<NavigationInfo>()
        traversal { item, depth ->
            list.add(item)
        }
        return list
    }
}