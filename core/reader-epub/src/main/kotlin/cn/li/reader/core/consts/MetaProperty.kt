package cn.li.reader.core.consts

/**
 * 依据 [Epub3.0文档-4.3.2 Metadata meta Properties](https://idpf.org/epub/30/spec/epub30-publications.html)
 *
 * */
object MetaProperty {
    // dc:title、dc:creator
    /**
     *
     * The display-seq property indicates the numeric position in which to display the current property relative to identical metadata properties (e.g., to indicate the order in which to render multiple titles).
     *
     * When the display-seq property is attached to some, but not all, of the members in a set, only the elements identified as having a sequence should be included in any rendering.
     *
     * 类型为 unsigned int
     * */
    const val DISPLAY_SEQ = "display-seq"

    // dc:creator
    /**
     * The alternate-script property provides an alternate expression of the associated property value in a language and script identified by the xml:lang attribute.
     *
     * This property is typically attached to creator and title properties for internationalization purposes.
     *
     * 类型为 string
     * */
    const val ALTERNATE_SCRIPT = "alternate-script"

    /**
     * The file-as property provides the normalized form of the associated property for sorting.
     *
     * 类型为 string
     * */
    const val FILE_AS = "file-as"

    /**
     * The group-position property indicates the numeric position in which the Publication is ordered relative to other works belonging to the same group (whether all EPUBs or not).
     *
     * The group-position property can be attached to any metadata property that establishes the group (such as a series title).
     *
     * A Publication can belong to more than one group.
     *
     * 类型为 unsigned-int 或 序列号如 1.2
     *
     * */
    const val GROUP_POSITION = "group-position"

    /**
     * The identifier-type property indicates the form or nature of an identifier.
     *
     * When the identifier-type value is drawn from a code list or other formal enumeration, the scheme attribute should be attached to identify its source.
     *
     * 类型为 string
     *
     * 仅适用 `<dc:identifier>`
     * */
    const val IDENTIFIER_TYPE = "identifier-type"

    /**
     * The meta-auth property provides the name of a party or authority responsible for an instance of package metadata.
     *
     * 类型为 string
     *
     * */
    const val META_AUTH = "meta-auth"

    /**
     * The role property describes the nature of work performed by a creator or contributor (e.g., that the person is the author or editor of a work).
     *
     * When the role value is drawn from a code list or other formal enumeration, the scheme attribute should be attached to identify its source.
     *
     * 类型为 string
     *
     * 仅适用于 `<dc:contribute>` 和 `<dc:creator>`
     * */
    const val ROLE = "role"

    /**
     *
     *
     * The title-type property indicates the form or nature of a title.
     *
     * When the title-type value is drawn from a code list or other formal enumeration, the scheme attribute should be attached to identify its source.
     * When a scheme is not specified, Reading Systems should recognize the following title type values: main, subtitle, short, collection, edition and expanded.
     *
     * 类型为 string
     *
     * 仅适用于 `<dc:title>`
     * */
    const val TITLE_TYPE = "title-type"


    /**
     * 用于标识最后修改时间
     *
     * */
    const val MODIFIED = "dcterms:modified"

}