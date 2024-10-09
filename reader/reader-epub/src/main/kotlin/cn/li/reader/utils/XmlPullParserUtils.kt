package cn.li.reader.utils

import org.kxml2.io.KXmlParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

/**
 *
 *
 * @author Grimrise 2024/9/23
 */

object XmlPullParserUtils {

    fun newParser(ins: InputStream, encoding: String = "utf-8"): XmlPullParser {
        return try {
            val factory = XmlPullParserFactory.newInstance()
            factory.newPullParser()
        } catch (e: Exception) {
            KXmlParser()
        }.apply {
            setInput(ins, encoding)
        }
    }

    inline fun XmlPullParser.loopNext(action: (eventType: Int) -> Unit) {
        while (eventType != XmlPullParser.END_DOCUMENT) {
            action(eventType)
            next()
        }
    }

    val XmlPullParser.isEndTag get() = eventType == XmlPullParser.END_TAG
    val XmlPullParser.isStartTag get() = eventType == XmlPullParser.START_TAG
    val XmlPullParser.isText get() = eventType == XmlPullParser.TEXT
    val XmlPullParser.isStartDocument get() = eventType == XmlPullParser.START_DOCUMENT
    val XmlPullParser.isEndDocument get() = eventType == XmlPullParser.END_DOCUMENT
    val XmlPullParser.isNotEndDocument get() = eventType != XmlPullParser.END_DOCUMENT


}