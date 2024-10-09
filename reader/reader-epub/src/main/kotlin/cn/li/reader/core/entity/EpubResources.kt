package cn.li.reader.core.entity

import cn.li.reader.core.entity.base.EpubResource

/**
 * Epub 中的所有资源集合
 *
 * @author Grimrise 2024/9/24
 */
class EpubResources {

    companion object {
        const val ID_NAV = "nav"
        const val ID_NCX = "ncx"
    }

    private val mResourcesByHref = HashMap<String, EpubResource>()
    private val mResourcesById = HashMap<String, EpubResource>()

    fun addResource(resource: EpubResource) {
        mResourcesByHref[resource.href] = resource
        resource.id?.let {
            mResourcesById[it] = resource
        }
    }

    fun updateResource(resource: EpubResource) {
        val exist = mResourcesByHref[resource.href]
        if (exist == null) {
            addResource(resource)
        } else {
            exist.id = resource.id
            exist.fallback = resource.fallback
            exist.properties = resource.properties
            exist.mediaOverlay = resource.mediaOverlay
            exist.mediaType = resource.mediaType
        }
    }

    fun getResourceByHref(href: String) = mResourcesByHref[href]

    fun getResourceById(id: String) = mResourcesById[id]

    fun remove(idOrHref: String): EpubResource? {
        var result = mResourcesById.remove(idOrHref)
        if (result != null) {
            mResourcesByHref.remove(result.href)
            return result
        }
        result = mResourcesByHref.remove(idOrHref)
        if (result != null) {
            mResourcesById.remove(result.id)
            return result
        }
        return null
    }

    /**
     * 返回 Navigation Document 资源，如果是 3.x 版本优先返回 nav 文档
     *
     * */
    fun getNavigationResourceV3() = mResourcesById[ID_NAV]

    fun getNavigationResourceV2() = mResourcesById[ID_NCX]
}