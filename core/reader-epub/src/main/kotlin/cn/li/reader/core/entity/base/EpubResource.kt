package cn.li.reader.core.entity.base

import cn.li.reader.core.consts.MediaType
import cn.li.reader.utils.toByteArray
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 *
 *
 * @author Grimrise 2024/09/24
 * */
class EpubResource(
    var href: String,
    private var data: ByteArray = ByteArray(0)
) {

    var id: String? = null
    var mediaType: MediaType? = null
    var fallback: String? = null
    var properties: String? = null
    var mediaOverlay: String? = null

    constructor(href: String, ins: InputStream): this(href, ins.toByteArray())

    constructor(href: String, id: String, mediaType: String, fallback: String?, properties: String?, mediaOverlay: String?) : this(href) {
        this.id = id
        this.mediaType = MediaType.valuesOf(mediaType)
        this.fallback = fallback
        this.properties = properties
        this.mediaOverlay = mediaOverlay
    }

    fun asInputStream() = ByteArrayInputStream(data)

    fun isDataLoad() = data.isNotEmpty()

    override fun toString(): String {
        return "EpubResource(id=$id, href=$href, mediaType=$mediaType, fallback=$fallback, properties=$properties, mediaOverlay=$mediaType, loaded=${isDataLoad()})"
    }




}
