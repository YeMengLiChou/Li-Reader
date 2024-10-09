package cn.li.reader.core.parse

import cn.li.reader.core.entity.base.AdditionalMeta
import cn.li.reader.core.entity.Author
import cn.li.reader.core.entity.Contributor
import cn.li.reader.core.entity.EpubBook
import cn.li.reader.core.entity.EpubMetadata
import cn.li.reader.core.entity.Identifier
import cn.li.reader.core.entity.base.Meta
import cn.li.reader.core.entity.Spine
import cn.li.reader.core.entity.SpineItem
import cn.li.reader.core.entity.Title
import cn.li.reader.core.entity.base.EpubResource
import cn.li.reader.core.exception.EpubParseException
import cn.li.reader.core.interfaces.IEpubProcessor
import cn.li.reader.utils.EpubProcessStats
import cn.li.reader.utils.ProcessorUtils
import cn.li.reader.utils.XmlPullParserUtils
import cn.li.reader.utils.XmlPullParserUtils.loopNext
import cn.li.reader.utils.getLine
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import kotlin.jvm.Throws

/**
 * Package Document 解析器
 *
 * @author Grimrise 2024/9/23
 */
class PackageDocProcessor private constructor()
    : IEpubProcessor{

    companion object {
        const val TAG_PACKAGE = "package"
        const val TAG_METADATA = "metadata"
        const val TAG_MANIFEST = "manifest"
        const val TAG_SPINE = "spine"
        const val TAG_GUIDE = "guide"
        const val TAG_BINDINGS = "bindings"

        // metadata sub elements
        const val TAG_OPF_META = "opf:meta"
        const val TAG_META = "meta"
        const val TAG_LINK = "link"
        const val TAG_DC_IDENTIFIER = "dc:identifier"
        const val TAG_DC_TITLE = "dc:title"
        const val TAG_DC_LANGUAGE = "dc:language"
        const val TAG_DC_CONTRIBUTOR = "dc:contributor"
        const val TAG_DC_CREATOR = "dc:creator"
        const val TAG_DC_DATE = "dc:date"
        const val TAG_DC_SOURCE = "dc:source"
        const val TAG_DC_TYPE = "dc:type"
        const val TAG_DC_COVERAGE = "dc:coverage"
        const val TAG_DC_DESCRIPTION = "dc:description"
        const val TAG_DC_FORMAT = "dc:format"
        const val TAG_DC_PUBLISHER = "dc:publisher"
        const val TAG_DC_RIGHTS = "dc:rights"
        const val TAG_DC_SUBJECT = "dc:subject"
        const val TAG_DC_RELATION = "dc:relation"

        const val TAG_ITEM = "item"
        const val TAG_ITEMREF = "itemref"



        // common
        const val ATTR_ID = "id"
        const val ATTR_DIR = "dir"
        const val ATTR_XML_LANG = "xml:lang"
        const val ATTR_REFINES = "refines"
        const val ATTR_PROPERTY = "property"
        const val ATTR_SCHEME = "scheme"
        const val ATTR_NAME = "name"
        const val ATTR_CONTENT = "content"
        const val ATTR_PROPERTIES = "properties"
        // package
        const val ATTR_VERSION = "version"
        const val ATTR_UNIQUE_IDENTIFIER = "unique-identifier"
        const val ATTR_PREFIX = "prefix"
        // item
        const val ATTR_HREF = "href"
        const val ATTR_MEDIA_TYPE = "media-type"
        const val ATTR_FALLBACK = "fallback"
        const val ATTR_MEDIA_OVERLAY = "media-overlay"
        // spine
        const val ATTR_TOC = "toc"
        const val ATTR_PAGE_PROGRESSION_DIRECTION = "page-progression-direction"
        // itemref
        const val ATTR_IDREF = "idref"
        const val ATTR_LINEAR = "linear"

        val inst by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { PackageDocProcessor() }
    }


    /**
     * 解析 Package Document，能获取到该 EPUB 文件的所有资源数据
     * TODO：在该部分兼容2.x的解析逻辑
     * */
    @Throws(EpubParseException::class)
    override fun process(book: EpubBook) {
//        EpubProcessStats.onProcess

        val xmlParser = XmlPullParserUtils.newParser(book.packageDocument.asInputStream())
        try {
            xmlParser.loopNext { eventType ->
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when(xmlParser.name) {
                            // <package>
                            TAG_PACKAGE -> {
                                book.version = xmlParser.getAttributeValue(null, ATTR_VERSION)
                                book.uniqueIdentifierScheme = xmlParser.getAttributeValue(null, ATTR_UNIQUE_IDENTIFIER)
                                book.prefix = xmlParser.getAttributeValue(null, ATTR_PREFIX)
                            }
                            // <metadata>
                            TAG_METADATA -> {
                                book.metadata = parseMetadata(xmlParser)
                            }
                            // <manifest>
                            TAG_MANIFEST -> {
                                parseManifestAndUpdate(xmlParser, book)
                            }
                            // <spine>
                            TAG_SPINE -> {
                                book.spine = parseSpine(xmlParser, book)!!
                                return
                            }
                            // <guide>
                            TAG_GUIDE -> {
                                // ignored
                            }
                            // <bindings>
                            TAG_BINDINGS -> {
                                // 暂不实现
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw ProcessorUtils.handleParseException(e, xmlParser, book.packageDocument)
        } finally {
//            xmlParser
        }
    }


    /**
     * 解析 <metadata> 标签内容
     * @param xmlParser
     * @return [EpubMetadata] 包含了该 EPUB 文件的元数据信息
     * */
    private fun parseMetadata(xmlParser: XmlPullParser): EpubMetadata {
        EpubProcessStats.onProcessMetadataStart()

        // 用于将 meta 补充信息附加
        val additionalMetas = mutableListOf<AdditionalMeta>()
        // 非附加信息的 meta
        val primaryMetas = HashMap<String, Meta>()
        val identifiers = mutableListOf<Identifier>()
        val titles = mutableListOf<Title>()
        val languages = mutableListOf<String>()
        val metadata = EpubMetadata(identifiers, titles, languages)

        while (xmlParser.eventType != XmlPullParser.END_DOCUMENT) {
            when (xmlParser.next()) {
                XmlPullParser.START_TAG -> {
                    when (xmlParser.name) {
                        TAG_DC_IDENTIFIER -> {
                             Identifier(
                                id = xmlParser.getAttributeValue(null, ATTR_ID),
                                value = xmlParser.nextText()
                            ).also {
                                 identifiers.add(it)
                                 additionalMetas.add(it)
                             }
                        }
                        TAG_DC_TITLE -> {
                            // 注意顺序
                            val dir = xmlParser.getAttributeValue(null, ATTR_DIR)
                            val id = xmlParser.getAttributeValue(null, ATTR_ID)
                            Title(title = xmlParser.nextText(), id = id, direction = Title.getDirection(dir) ).also {
                                titles.add(it)
                                additionalMetas.add(it)
                            }
                        }
                        TAG_DC_LANGUAGE -> {
                            val language = xmlParser.nextText()
                            languages.add(language)
                        }
                        TAG_DC_CONTRIBUTOR -> {
                            val id = xmlParser.getAttributeValue(null, ATTR_ID)
                            Contributor(name = xmlParser.nextText(), id = id).also {
                                metadata.addContributor(it)
                                additionalMetas.add(it)
                            }
                        }
                        TAG_DC_CREATOR -> {
                            val id = xmlParser.getAttributeValue(null, ATTR_ID)
                            Author(name = xmlParser.nextText(), id = id).also {
                                metadata.addAuthor(it)
                                additionalMetas.add(it)
                            }
                        }
                        TAG_DC_DATE -> metadata.setPublishDate(date = xmlParser.nextText())
                        TAG_DC_SOURCE -> metadata.setSource(source = xmlParser.nextText())
                        TAG_DC_TYPE -> metadata.setType(type = xmlParser.nextText())
                        TAG_DC_COVERAGE -> metadata.addCoverage(coverage = xmlParser.nextText())
                        TAG_DC_DESCRIPTION -> metadata.addDescription(desc = xmlParser.nextText())
                        TAG_DC_FORMAT -> metadata.addFormats(format = xmlParser.nextText())
                        TAG_DC_PUBLISHER -> metadata.addPublisher(publisher = xmlParser.nextText())
                        TAG_DC_RELATION -> metadata.addRelation(relation = xmlParser.nextText())
                        TAG_DC_SUBJECT -> metadata.addSubject(subject = xmlParser.nextText())
                        TAG_DC_RIGHTS -> metadata.addRights(rights = xmlParser.nextText())
                        TAG_OPF_META, TAG_META -> {
                            val meta = this.parseMeta(xmlParser) ?: continue
                            if (meta.refines != null) {
                                addOpfMetaTo(additionalMetas, meta, meta.refines)
                            } else {
                                primaryMetas[meta.property] = meta
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (xmlParser.name == TAG_METADATA) {
                        break
                    }
                }
            }
        }

        EpubProcessStats.onProcessMetadataEnd()
        return metadata.apply {
            setMeta(primaryMetas)
        }
    }

    /**
     * 解析 `<opf:meta>` 或 `<meta property="xxx">xxx</meta>` 标签内容
     * @param xmlParser
     * @return [Meta] 当前 Tag 不符合 [XmlPullParser.START_TAG] 时或者为 opf2 的 meta 时，返回 null
     * */
    private fun parseMeta(xmlParser: XmlPullParser): Meta? {
        if (xmlParser.eventType != XmlPullParser.START_TAG) return null

        // opf2 定义的 meta 在 epub3 中应该被忽略
        val name = xmlParser.getAttributeValue(null, ATTR_NAME)
        if (name != null) return null

        val refines = xmlParser.getAttributeValue(null, ATTR_REFINES)
        val property = xmlParser.getAttributeValue(null, ATTR_PROPERTY)
        val scheme = xmlParser.getAttributeValue(null, ATTR_SCHEME)
        val id = xmlParser.getAttributeValue(null, ATTR_ID)
        return Meta(
            property = property,
            value = xmlParser.nextText(),
            refines = refines,
            scheme = scheme,
            id = id
        )
    }

    /**
     * 将 [meta] 添加到对应 [Meta.refines] 相同的 [Meta] 中
     * @param additionalMetas
     * @param meta
     * @param targetId
     * */
    private fun addOpfMetaTo(additionalMetas: List<AdditionalMeta>, meta: Meta, targetId: String) {
        val refineId = if (targetId.startsWith("#")) {
            targetId.substring(1)
        } else {
            targetId
        }
        additionalMetas.forEach {
            if (it.refinesId == refineId) {
                it[meta.property] = meta
            }
        }

    }


    /**
     * 解析 `<manifest>` 内容
     * @param xmlParser
     * */
    private fun parseManifestAndUpdate(xmlParser: XmlPullParser, book: EpubBook) {
        if (xmlParser.eventType != XmlPullParser.START_TAG || xmlParser.name != TAG_MANIFEST) return

        EpubProcessStats.onProcessManifestStart()

        while(xmlParser.eventType != XmlPullParser.END_DOCUMENT) {
            when(xmlParser.next()) {
                XmlPullParser.START_TAG -> {
                    if (xmlParser.name == TAG_ITEM) {
                        EpubResource(
                            id = xmlParser.getAttributeValue(null, ATTR_ID),
                            href = xmlParser.getAttributeValue(null, ATTR_HREF),
                            mediaType = xmlParser.getAttributeValue(null, ATTR_MEDIA_TYPE),
                            fallback = xmlParser.getAttributeValue(null, ATTR_FALLBACK),
                            properties = xmlParser.getAttributeValue(null, ATTR_PROPERTIES),
                            mediaOverlay = xmlParser.getAttributeValue(null, ATTR_MEDIA_OVERLAY)
                        ).also {
                            book.resources.updateResource(it)
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (xmlParser.name == TAG_MANIFEST) {
                        break
                    }
                }
                XmlPullParser.END_DOCUMENT -> break
            }
        }

        EpubProcessStats.onProcessManifestEnd()
    }

    /**
     * 解析 `<spine>` 的内容
     * @param xmlParser
     * @return [Spine]
     * */
    private fun parseSpine(xmlParser: XmlPullParser, book: EpubBook): Spine? {
        if (xmlParser.eventType != XmlPullParser.START_TAG || xmlParser.name != TAG_SPINE) return null

        EpubProcessStats.onProcessSpineStart()

        val items = mutableListOf<SpineItem>()
        val tocIdRef = xmlParser.getAttributeValue(null, ATTR_TOC)
        val pageProgressionDir = xmlParser.getAttributeValue(null, ATTR_PAGE_PROGRESSION_DIRECTION)
        while (xmlParser.eventType != XmlPullParser.END_DOCUMENT) {
            when(xmlParser.next()) {
                XmlPullParser.START_TAG -> {
                    if (xmlParser.name != TAG_ITEMREF) {
                        continue
                    }
                    val idref = xmlParser.getAttributeValue(null, ATTR_IDREF)
                    SpineItem(
                        idref = idref,
                        linear = xmlParser.getAttributeValue(null, ATTR_LINEAR) ?: "yes",
                        properties = xmlParser.getAttributeValue(null, ATTR_PROPERTIES),
                        resource = book.resources.getResourceById(idref)!!
                    ).also {
                        items.add(it)
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (xmlParser.name == TAG_SPINE) {
                        break
                    }
                }
                XmlPullParser.END_DOCUMENT -> break
            }
        }

        EpubProcessStats.onProcessSpineEnd()
        return Spine(items, tocIdRef, pageProgressionDir)
    }






















}