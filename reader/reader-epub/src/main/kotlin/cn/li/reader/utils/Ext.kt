package cn.li.reader.utils

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 *
 *
 * @author Grimrise 2024/9/23
 */

/**
 * 遍历zip文件
 * */
inline fun ZipInputStream.forEach(crossinline action: (entry: ZipEntry) -> Unit) {
    var entry = nextEntry
    while (entry != null) {
        action(entry)
        closeEntry()
        entry = nextEntry
    }
}


fun String.whitespaceCollapsing(): String {
    val result = StringBuilder()
    var preIdx = 0
    var idx = 0
    while (idx < length) {
        while (idx < length && !this[idx].isWhitespace()) {
            idx ++
        }
        result.append(substring(preIdx, idx))
        if (idx >= length) break
        while (idx < length && this[idx].isWhitespace()) {
            idx ++
        }
        result.append(' ')
        preIdx = idx
    }
    return result.toString()
}
