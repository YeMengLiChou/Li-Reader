package cn.li.reader.core.parse

import cn.li.reader.core.entity.EpubBook
import cn.li.reader.core.entity.NavigationInfo
import cn.li.reader.core.entity.base.EpubResource
import cn.li.reader.core.interfaces.IEpubProcessor
import cn.li.reader.utils.ProcessorUtils
import cn.li.reader.utils.XmlPullParserUtils
import cn.li.reader.utils.XmlPullParserUtils.loopNext
import cn.li.reader.utils.whitespaceCollapsing
import org.xmlpull.v1.XmlPullParser

/**
 * TODO：可能同一个文件会根据 id 划分不同的章节，如何进行处理？
 *
 * @author Grimrise 2024/9/25
 */
class NavigationDocProcessor: IEpubProcessor {

    companion object {
        // v3 nav
        private const val TAG_HEAD = "head"
        private const val TAG_TITLE = "title"
        private const val TAG_BODY = "body"
        private const val TAG_NAV = "nav"
        private const val TAG_A = "a"
        private const val TAG_SPAN = "span"
        private const val TAG_OL = "ol"
        private const val TAG_LI = "li"
        private const val TAG_HGROUP = "hgroup"

        private const val ATTR_TITLE = "title"
        private const val ATTR_HREF = "href"
        private const val ATTR_EPUB_TYPE = "epub:type"
        private const val ATTR_HIDDEN = "hidden"

        private const val TYPE_TOC = "toc"
        private const val TYPE_PAGE_LIST = "page-list"
        private const val TYPE_LANDMARKS = "landmarks"

        val inst by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { NavigationDocProcessor() }
    }

    override fun process(book: EpubBook) {
        if (book.isEpubVersion3x()) {
            val navResource = book.resources.getNavigationResourceV3()
            if (navResource != null) {
                book.navigationDocument = navResource
                parseNavV3(navResource)
                return
            }
        }

    }

    fun parseNavV3(resource: EpubResource): NavigationInfo? {
        val xmlParser = XmlPullParserUtils.newParser(resource.asInputStream())
        var docTitle: String? = null
        var navigationRoot: NavigationInfo? = null

        try {
            xmlParser.loopNext { eventType ->
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (xmlParser.name) {
                            TAG_TITLE -> {
                                if (docTitle == null) {
                                    docTitle = xmlParser.nextText().whitespaceCollapsing()
                                }
                            }
                            TAG_NAV -> {
                                val type = xmlParser.getAttributeValue(null, ATTR_EPUB_TYPE)
                                if (type == TYPE_TOC) {
                                    navigationRoot = parseNavWithToc(xmlParser)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw ProcessorUtils.handleParseException(e, xmlParser, resource)
        }
        return navigationRoot
    }

    /**
     * 解析 <nav epub:type="toc"> 标签
     * @param xmlPullParser
     * @return 返回一个树状结构的 [NavigationInfo] 根节点
     */
    private fun parseNavWithToc(xmlPullParser: XmlPullParser): NavigationInfo? {
        if (xmlPullParser.eventType != XmlPullParser.START_TAG || xmlPullParser.name != TAG_NAV) return null

        var order = 0
        val rootNav = NavigationInfo(order = order ++)
        var curNav: NavigationInfo? = null
        var parsingAContent = false
        val content = StringBuilder()
        while (xmlPullParser.eventType != XmlPullParser.END_DOCUMENT) {
            when (xmlPullParser.next()) {
                XmlPullParser.START_TAG -> {
                    val name = xmlPullParser.name
                    when(xmlPullParser.name) {
                        // <ol> 标识进入一个新的列表，可能是嵌套的
                        TAG_OL -> {
                            curNav!!.children = mutableListOf()
                        }
                        // <li> 新的子项
                        TAG_LI -> {
                            val child = NavigationInfo(order = order ++, parent = curNav)
                            curNav = child
                        }
                        // <span> 标题
                        TAG_SPAN -> {
                            curNav!!.title = xmlPullParser.nextText().whitespaceCollapsing()
                        }
                        // <a> 为实际内容
                        TAG_A -> {
                            requireNotNull(curNav)
                            curNav.src = xmlPullParser.getAttributeValue(null, ATTR_HREF)
                            curNav.hidden = xmlPullParser.getAttributeValue(null, ATTR_HIDDEN) != null
                            // a 标签如果没有文本，则必须要有 title 属性
                            curNav.title = xmlPullParser.getAttributeValue(null, ATTR_TITLE)
                            // a 标签文本内容可能存在嵌套的标签，不能直接 nextText
                            if (curNav.title == null) {
                                parsingAContent = true
                            }
                        }
                        else -> {
                            // TODO: 考虑要不要将 hgroup 中的 p 标签内容加上
                            if (!name.startsWith("h") || name == TAG_HGROUP) continue
                            // 根节点的标题
                            rootNav.title = xmlPullParser.nextText().whitespaceCollapsing()
                            curNav = rootNav
                        }
                    }
                }

                XmlPullParser.TEXT -> {
                    if (xmlPullParser.isWhitespace) continue
                    if (parsingAContent) {
                        content.append(xmlPullParser.text.whitespaceCollapsing())
                    }
                }

                XmlPullParser.END_TAG -> {
                    when(xmlPullParser.name) {
                        TAG_NAV -> break
                        TAG_LI -> {
                            // 当前子项已经结束，需要将它添加到父节点中
                            val parent = requireNotNull(curNav).parent
                            parent!!.children!!.add(curNav)
                            curNav = parent
                        }
                        TAG_A -> {
                            // 标题部分
                            if (parsingAContent) {
                                parsingAContent = false
                                curNav!!.title = content.toString()
                                content.clear()
                            }
                        }
                    }
                }
            }
        }
        rootNav.traversal { item, depth ->
                println("${"\t".repeat(depth)}${item.title} [$depth]")
            }
            println()
        return rootNav
    }

}