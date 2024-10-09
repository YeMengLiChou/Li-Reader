package cn.li.reader.utils

import cn.li.reader.core.entity.base.EpubResource
import cn.li.reader.core.exception.EpubParseException
import cn.li.reader.utils.XmlPullParserUtils.isEndDocument
import cn.li.reader.utils.XmlPullParserUtils.isEndTag
import cn.li.reader.utils.XmlPullParserUtils.isNotEndDocument
import cn.li.reader.utils.XmlPullParserUtils.isStartTag
import cn.li.reader.utils.XmlPullParserUtils.isText
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import kotlin.math.max

object ProcessorUtils {

    fun handleParseException(error: Exception, xmlParser: XmlPullParser? = null, resource: EpubResource? = null): EpubParseException {
        if (error is EpubParseException) {
            return error
        }

        if (xmlParser == null || resource == null) {
            return EpubParseException.from(EpubParseException.ERROR_UNKNOWN, throwable =  error)
        }

        val lineNumber = xmlParser.lineNumber
        val columnNumber = xmlParser.columnNumber
        val type = if (xmlParser.eventType == XmlPullParser.START_TAG || xmlParser.eventType == XmlPullParser.END_TAG) {
            xmlParser.name
        } else if (xmlParser.eventType == XmlPullParser.TEXT) {
            xmlParser.text
        } else {
            "unknown type"
        }
        val line: String = if (xmlParser.lineNumber == -1) {
            "[unknown]"
        } else {
            // 如果是 tag start，则继续读取
            when (xmlParser.eventType) {
                XmlPullParser.START_TAG -> resource.asInputStream().use {
                            getErrorTextByStartTag(xmlParser, it)
                        }
                XmlPullParser.END_TAG -> resource.asInputStream().use {
                        getErrorTextByEndTag(
                            errParser = xmlParser,
                            xmlParser = XmlPullParserUtils.newParser(resource.asInputStream()),
                            inputStream = it
                        )
                    }
                XmlPullParser.TEXT -> {
                    while (xmlParser.eventType != XmlPullParser.END_DOCUMENT && xmlParser.eventType != XmlPullParser.END_TAG) {
                        xmlParser.next()
                    }
                    resource.asInputStream().use {
                        getErrorTextByEndTag(
                            errParser = xmlParser,
                            xmlParser = XmlPullParserUtils.newParser(it),
                            it
                        )
                    }
                }
                else -> {
                    "[unknown]"
                }
            }
        }
        return EpubParseException.from(EpubParseException.ERROR_PARSE, "line $lineNumber, col $columnNumber, tag $type, $line", error)
    }

    private fun getErrorTextByStartTag(xmlParser: XmlPullParser, inputStream: InputStream): String {
        val name = xmlParser.name
        val content = StringBuilder()
        var curIdx = 0
        var line: String?
        val reader = inputStream.bufferedReader()
        var lineNumber = xmlParser.lineNumber
        var columnNumber = xmlParser.columnNumber
        do {
            line = reader.readLine()
            curIdx ++
        } while (curIdx <= lineNumber - 1 && line != null)
        // now line is linenumber
        line?.substring(0, columnNumber - 1)?.let {
            val startIndex = it.lastIndexOf("<$name")
            content.append(it.substring(startIndex))
        }
        var repeatCount = 0
        while (!(xmlParser.eventType != XmlPullParser.END_DOCUMENT || (xmlParser.eventType == XmlPullParser.END_TAG && xmlParser.name == name && repeatCount == 0))) {
            if (xmlParser.lineNumber != lineNumber) {
                lineNumber = xmlParser.lineNumber
                columnNumber = 0
                line = reader.readLine()
            }
            if (columnNumber != xmlParser.columnNumber) {
                content.append(line?.substring(columnNumber, xmlParser.columnNumber - 1))
                columnNumber = xmlParser.columnNumber
            }

            if (xmlParser.eventType == XmlPullParser.START_TAG && xmlParser.name == name) {
                repeatCount ++
            }
            if (xmlParser.eventType == XmlPullParser.END_TAG && xmlParser.name == name) {
                repeatCount --
            }
            xmlParser.next()
        }
        return content.toString()
    }

    private fun getErrorTextByEndTag(errParser: XmlPullParser, xmlParser: XmlPullParser, inputStream: InputStream): String {
        val errName = errParser.name
        val errLineNumber = errParser.lineNumber
        val errDepth = errParser.depth
        var curIdx = 0
        var line: String? = ""
        val content = StringBuilder()
        val reader = inputStream.bufferedReader()
        var lineNumber = 1
        var columnNumber = 1
        var needAppend = false
        var depth = 0

        while (xmlParser.isNotEndDocument) {
            if (xmlParser.isStartTag && xmlParser.name == errName && xmlParser.depth == errDepth) {
                // 从此处开始读取内容
                needAppend = true
                depth = 0
                if (lineNumber != xmlParser.lineNumber) {
                    columnNumber = 1
                    lineNumber = xmlParser.lineNumber
                    while (curIdx < lineNumber && line != null) {
                        line = reader.readLine()
                        curIdx ++
                    }
                }
            }

            if (needAppend) {
                if (lineNumber != xmlParser.lineNumber) {
                    lineNumber = xmlParser.lineNumber
                    columnNumber = 1
                    while (curIdx < lineNumber && line != null) {
                        line = reader.readLine()
                        curIdx ++
                    }
                }

                if (columnNumber != xmlParser.columnNumber) {
                    if (xmlParser.isText && xmlParser.text.isBlank()) {
                        columnNumber = xmlParser.columnNumber
                        continue
                    }
                    if (xmlParser.isEndTag) {
                        depth --
                    }
                    content.append("\t".repeat(depth))
                    if (xmlParser.isStartTag) {
                        depth ++
                    }
                    if (xmlParser.isText) {
                        content.append(xmlParser.text.trim())
                    } else if (xmlParser.isStartTag) {
                        content.append(line!!.substring(max(0, columnNumber - 1), xmlParser.columnNumber - 1).trim('\n', ' '))
                    } else if (xmlParser.isEndTag) {
                        content.append("</${xmlParser.name}>")
                    }
                    content.append('\n')
                    columnNumber = xmlParser.columnNumber
                }
            }

            if (xmlParser.isEndTag && xmlParser.name == errName && xmlParser.depth == errDepth) {
                // 读取结束
                needAppend = false
                if (xmlParser.lineNumber >= errLineNumber) {
                    break
                } else {
                    content.clear()
                }
            }
            xmlParser.next()
        }
        return '\n' + content.toString()
    }

}