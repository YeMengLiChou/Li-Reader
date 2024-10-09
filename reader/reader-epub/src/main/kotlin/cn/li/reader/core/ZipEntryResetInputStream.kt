package cn.li.reader.core

import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * 重写 [InputStream.reset] 方法以方便复用
 *
 * @author Grimrise 2024/9/25
 */
class ResetInputStream(
    private val zipFile: ZipFile,
    private val entry: ZipEntry,
): InputStream() {

    private var curInputStream: InputStream? = null

    override fun read(): Int {
        if (curInputStream == null) {
            curInputStream = zipFile.getInputStream(entry)
        }
        return curInputStream!!.read()
    }

    override fun reset() {
        curInputStream?.close()
        curInputStream = null
        curInputStream = zipFile.getInputStream(entry)
    }

    override fun close() {
        curInputStream?.close()
        curInputStream = null
        // 这里不 close zipFile，避免其他地方还要用到
    }
}