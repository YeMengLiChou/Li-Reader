package cn.li.reader.core.parse

import cn.li.reader.core.consts.EpubConstants
import cn.li.reader.core.entity.EpubBook
import cn.li.reader.core.entity.EpubRootFile
import cn.li.reader.core.exception.EpubParseException
import cn.li.reader.core.interfaces.IEpubProcessor
import cn.li.reader.utils.EpubLog
import cn.li.reader.utils.EpubProcessStats
import cn.li.reader.utils.XmlPullParserUtils
import cn.li.reader.utils.XmlPullParserUtils.loopNext
import org.xmlpull.v1.XmlPullParser

/**
 * [META-INF/container.xml 格式](https://idpf.org/epub/30/spec/epub30-ocf.html#app-schema-container)
 * ```rnc
 *    default namespace = "urn:oasis:names:tc:opendocument:xmlns:container"
 *    include "./mod/datatypes.rnc"
 *    start = ocf.container
 *    ocf.container = element container {
 *       attribute version { '1.0' } &
 *       element rootfiles {
 *          element rootfile {
 *             attribute full-path { datatype.URI } &
 *             attribute media-type { 'application/oebps-package+xml' }
 *          }+
 *       }
 *    }
 *  ```
 * @author Grimrise 2024/9/23
 */
class ContainerProcessor private constructor() : IEpubProcessor {

    companion object {
        const val TAG = "ContainerProcessor"

        const val TAG_ROOT_FILES = "rootfiles"
        const val TAG_ROOT_FILE = "rootfile"

        const val ATTR_FULL_PATH = "full-path"
        const val ATTR_MEDIA_TYPE = "media-type"

        const val VALUE_MEDIA_TYPE = "application/oebps-package+xml"

        val inst by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ContainerProcessor() }
    }

    /**
     * 解析 META-INF/container.xml 文件
     * @return 解析到的 root file 文件列表
     * */
    @Throws(EpubParseException::class)
    override fun process(book: EpubBook) {
        val containerResource = book.resources.remove(EpubConstants.NAME_CONTAINER_XML)
            ?.also { book.containerDocument = it }
            ?: throw EpubParseException.from(
                EpubParseException.ERROR_INCORRECT_EPUB_FORMAT,
                "no `${EpubConstants.NAME_CONTAINER_XML}` "
            )

        val parser = XmlPullParserUtils.newParser(containerResource.asInputStream())
        val rootFiles = mutableListOf<EpubRootFile>()
        parser.loopNext { eventType ->
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    val name = parser.name
                    // rootfile 标签
                    if (name == TAG_ROOT_FILE) {
                        val fullPath = parser.getAttributeValue(null, ATTR_FULL_PATH)
                        val mediaType = parser.getAttributeValue(null, ATTR_MEDIA_TYPE)
                        if (fullPath == null) {
                            throw EpubParseException.from(EpubParseException.ERROR_NOT_FULL_PATH)
                        }
                        rootFiles.add(EpubRootFile(fullPath, mediaType))
                    }
                }
            }
        }

        // 没有 full-path
        if (rootFiles.isEmpty()) {
            throw EpubParseException.from(EpubParseException.ERROR_NOT_FULL_PATH)
        } else if (rootFiles.size > 1) {
            EpubLog.w(TAG, "${EpubConstants.NAME_CONTAINER_XML} has more than one root file: ${rootFiles}")
        }

        val packageDocumentPath = rootFiles.first().fullPath
        val packageDocumentResource = book.resources.remove(packageDocumentPath)
            ?: throw EpubParseException.from(
                EpubParseException.ERROR_INCORRECT_EPUB_FORMAT,
                "no declared package document `${packageDocumentPath}`"
            )
        book.packageDocument = packageDocumentResource

    }
}