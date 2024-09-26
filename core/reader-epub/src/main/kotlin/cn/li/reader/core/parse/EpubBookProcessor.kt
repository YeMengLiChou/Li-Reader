package cn.li.reader.core.parse

import cn.li.reader.core.consts.EpubConstants
import cn.li.reader.core.entity.EpubBook
import cn.li.reader.core.entity.EpubResources
import cn.li.reader.core.entity.base.EpubResource
import cn.li.reader.core.exception.EpubParseException
import cn.li.reader.utils.EpubLog
import cn.li.reader.utils.EpubProcessStats
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipException
import java.util.zip.ZipFile

/**
 *
 *  TODO: 检查 namespace？ container.xml 的文件 mimetype
 *  TODO：当无法定位 full-path 时通过可视化界面展示，让其选择对应的文件？
 * @author Grimrise 2024/9/22
 */
class EpubBookProcessor (
    private val path: String,
) {
    companion object {
        const val TAG = "EpubBookProcessor"
    }

    private lateinit var mZipFile: ZipFile

    init {
        try {
            EpubLog.i(TAG, "the epub path: $path")
            mZipFile = ZipFile(path)
        } catch (e: ZipException) {

        } catch (e: IOException) {

        }
    }


    fun process(): EpubBook? {
        EpubProcessStats.reset()
        EpubProcessStats.onProcessStart()

        val book = EpubBook(path).apply {
            this.resources = processZipResources()
        }

        val mimetype = book.resources.remove(EpubConstants.NAME_MIMETYPE)
        val isEpubFormat = mimetype != null && verifyMimetype(
            mimetype.asInputStream()
        )

        // mimetype 不匹配
        if (!isEpubFormat) {
            throw EpubParseException.from(
                EpubParseException.ERROR_INCORRECT_EPUB_FORMAT,
                "mimetype don't match!"
            )
        }

        // META-INF/container.xml
        EpubProcessStats.onProcessContainerStart()
        ContainerProcessor.inst.process(book)
        EpubProcessStats.onProcessContainerEnd()


        // Package Document
        EpubProcessStats.onProcessPackageDocStart()
        PackageDocProcessor.inst.process(book)
        EpubProcessStats.onProcessPackageDocEnd()

        // Navigation Document
        EpubProcessStats.onProcessNavigationDocumentStart()
        NavigationDocProcessor.inst.process(book)
        EpubProcessStats.onProcessNavigationDocumentEnd()

        EpubProcessStats.onProcessEnd()
        EpubLog.i(TAG, EpubProcessStats.getStatsString())
        return book
    }

    private fun processZipResources(): EpubResources {
        EpubProcessStats.onProcessZipStart()

        val entries = mZipFile.entries()
        val resources = EpubResources()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            // TODO: 考虑转换为 Lazy 初始化
            resources.addResource(EpubResource(href = entry.name, ins = mZipFile.getInputStream(entry)))
        }

        EpubProcessStats.onProcessZipEnd()
        return resources
    }


    /**
     * 检验 Epub 格式
     * @return 如果是 Epub 格式返回 true；
     * */
    private fun verifyMimetype(ins: InputStream): Boolean {
        return ins.bufferedReader().use { it.readLine() == EpubConstants.MIMETYPE_EPUB }
    }

}


