package cn.li.reader.core.consts

/**
 * 媒体类型
 * */
enum class MediaType(
    val type: String,
    val defaultExtension: String,
    val extensions: Array<String> = arrayOf()
) {
    // ===== Core Media Types by EPUB 3.0 Specification ====
    // image
    PNG("image/png", ".png"),
    JPEG("image/jpeg", "jpeg", arrayOf("jpg", "jpeg")),
    GIF("image/gif", ".gif"),
    SVG("image/svg+xml", ".svg"),
    // applications
    XHTML("application/xhtml+xml", ".xhtml", arrayOf(".htm", ".html", ".xhtml")),
    NCX("application/x-dtbncx+xml", ".ncx"),
    PLS("application/pls+xml", ".pls"),
    SMIL("application/smil+xml", ".smil"),
    WOFF("application/font-woff", ".woff"),
    OPEN_TYPE("application/vnd.ms-opentype", ".otf"),
    // audio
    MP3("audio/mpeg", ".mp3"),
    MP4("audio/mp4", ".mp4"),
    // text
    CSS("text/css", ".css"),
    JS("text/javascript", ".js");

    /**
     * 判断是否为 [Core Media Types](https://idpf.org/epub/30/spec/epub30-publications.html)
     * @return 若是则返回 true, 否则返回 false
     * */
    fun isCoreMediaType(): Boolean {
        return when (this) {
            cn.li.reader.core.consts.MediaType.PNG, cn.li.reader.core.consts.MediaType.JPEG, cn.li.reader.core.consts.MediaType.GIF, cn.li.reader.core.consts.MediaType.SVG, cn.li.reader.core.consts.MediaType.XHTML, cn.li.reader.core.consts.MediaType.NCX, cn.li.reader.core.consts.MediaType.PLS, cn.li.reader.core.consts.MediaType.SMIL, cn.li.reader.core.consts.MediaType.WOFF, cn.li.reader.core.consts.MediaType.OPEN_TYPE, cn.li.reader.core.consts.MediaType.MP3, cn.li.reader.core.consts.MediaType.MP4, cn.li.reader.core.consts.MediaType.CSS, cn.li.reader.core.consts.MediaType.JS -> true
            else -> false
        }
    }

    companion object {
        fun valuesOf(type: String): cn.li.reader.core.consts.MediaType? {
            return entries.find { it.type == type }
        }
    }

}
