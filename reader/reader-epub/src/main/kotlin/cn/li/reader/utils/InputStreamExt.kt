package cn.li.reader.utils

import java.io.ByteArrayOutputStream
import java.io.InputStream

private const val DEFAULT_BUF_SIZE = 1024 * 8


/**
 * 将 InputStream 的所有内容读取到 ByteArray 中
 * @param bufSize 缓冲区大小，默认 8KB
 * */
fun InputStream.toByteArray(bufSize: Int = DEFAULT_BUF_SIZE): ByteArray {
    return ByteArrayOutputStream(bufSize).apply {
        copyTo(this)
        flush()
    }.toByteArray()
}

/**
 * 返回流中第 index 行的内容，从 0 开始；
 * 请注意保证 [InputStream] 是从头开始的；
 * */
fun InputStream.getLine(index: Int): String? {
    var curIdx = 0
    var line: String?
    val reader = this.bufferedReader()
    do {
        line = reader.readLine()
        curIdx ++
    } while (curIdx <= index && line != null)
    return line
}