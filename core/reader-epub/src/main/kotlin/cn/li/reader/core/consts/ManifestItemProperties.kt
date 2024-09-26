package cn.li.reader.core.consts

/**
 * Manifest 清单 item 元素的 properties 属性值
 *
 * See: [Epub3.0 Specification](https://idpf.org/epub/30/spec/epub30-publications.html#sec-item-property-values)
 *
 * */
object ManifestItemProperties {

    /**
     * The cover-image property identifies the described Publication Resource as the cover image for the Publication.
     *
     *  0 or 1
     * */
    const val COVER_IMAGE = "cover-image"

    /**
     * The mathml property indicates that the described Publication Resource contains one or more instances of MathML markup.
     *
     * 0 or more
     * */
    const val MATHML = "mathml"

    /**
     * The nav property indicates that the described Publication Resource constitutes the EPUB Navigation Document of the Publication.
     *
     * Exactly 1
     * */
    const val NAV = "nav"

    /**
     * The remote-resources property indicates that the described Publication Resource contains one or more internal references to other Publication Resources that are located outside of the EPUB Container.
     *
     * 0 or more
     * */
    const val REMOTE_RESOURCES = "remote-resources"

    /**
     * The scripted property indicates that the described Publication Resource is a Scripted Content Document (i.e., contains scripted content and/or elements from HTML5 forms).
     *
     * 0 or more
     * */
    const val SCRIPTED = "scripted"

    /**
     * The svg property indicates that the described Publication Resource contains one or more instances of SVG markup.
     *
     * 0 or more
     * */
    const val SVG = "svg"

    /**
     * The switch property indicates that the described Publication Resource contains one or more instances of the epub:switch element.
     *
     * 0 or more
     * */
    const val SWITCH = "switch"
}