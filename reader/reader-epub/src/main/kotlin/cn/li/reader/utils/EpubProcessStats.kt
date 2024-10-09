package cn.li.reader.utils

import android.os.SystemClock

/**
 *
 * 统计 Epub 解析耗时
 * @author Grimrise 2024/9/26
 * */
object EpubProcessStats {
    
    private val isAndroid = System.getProperty("java.vm.name")?.startsWith("Dalvik") ?: false

    private var processStartTime: Long = 0L
    private var processZipStartTime: Long = 0L
    private var processZipEndTime: Long = 0L
    private var processContainerStartTime: Long = 0L
    private var processContainerEndTime: Long = 0L
    private var processPackageDocStartTime: Long = 0L
    private var processPackageDocEndTime: Long = 0L
    private var processMetadataStartTime: Long = 0L
    private var processMetadataEndTime: Long = 0L
    private var processManifestStartTime: Long = 0L
    private var processManifestEndTime: Long = 0L
    private var processSpineStartTime: Long = 0L
    private var processSpineEndTime: Long = 0L
    private var processNavigationDocumentStartTime: Long = 0L
    private var processNavigationDocumentEndTime: Long = 0L
    private var processEndTime: Long = 0L

    private fun getTime() = if (isAndroid) SystemClock.elapsedRealtime() else System.currentTimeMillis()
    
    fun onProcessStart() {
        processStartTime = getTime()
    }

    fun onProcessEnd() {
        processEndTime = getTime()
    }

    fun onProcessZipStart() {
        processZipStartTime = getTime()
    }

    fun onProcessZipEnd() {
        processZipEndTime = getTime()
    }

    fun onProcessContainerStart() {
        processContainerStartTime = getTime()
    }

    fun onProcessContainerEnd() {
        processContainerEndTime = getTime()
    }

    fun onProcessPackageDocStart() {
        processPackageDocStartTime = getTime()
    }

    fun onProcessPackageDocEnd() {
        processPackageDocEndTime = getTime()
    }

    fun onProcessMetadataStart() {
        processMetadataStartTime = getTime()
    }

    fun onProcessMetadataEnd() {
        processMetadataEndTime = getTime()
    }

    fun onProcessManifestStart() {
        processManifestStartTime = getTime()
    }

    fun onProcessManifestEnd() {
        processManifestEndTime = getTime()
    }

    fun onProcessSpineStart() {
        processSpineStartTime = getTime()
    }
    fun onProcessSpineEnd() {
        processSpineEndTime = getTime()
    }

    fun onProcessNavigationDocumentStart() {
        processNavigationDocumentStartTime = getTime()
    }

    fun onProcessNavigationDocumentEnd() {
        processNavigationDocumentEndTime = getTime()
    }

    fun reset() {
        processStartTime = 0L
        processEndTime = 0L
        processZipStartTime = 0L
        processZipEndTime = 0L
        processPackageDocStartTime = 0L
        processPackageDocEndTime = 0L
        processContainerStartTime = 0L
        processContainerEndTime = 0L
        processMetadataStartTime = 0L
        processMetadataEndTime = 0L
        processManifestStartTime = 0L
        processManifestEndTime = 0L
        processSpineStartTime = 0L
        processSpineEndTime = 0L
        processNavigationDocumentStartTime = 0L
        processNavigationDocumentEndTime = 0L
    }

    fun getProcessTime(): Long {
        return processEndTime - processStartTime
    }

    fun getStatsString(): String {
        return "process time: ${getProcessTime()}ms, \n" +
                "\tzip time: ${processZipEndTime - processZipStartTime}ms, \n" +
                "\tcontainer time: ${processContainerEndTime - processContainerStartTime}ms, \n" +
                "\tpackage time: ${processPackageDocEndTime - processPackageDocStartTime}ms " +
                "(metadata: ${processMetadataEndTime - processMetadataStartTime}ms, " +
                "manifest: ${processManifestEndTime - processManifestStartTime}ms, " +
                "spine: ${processSpineEndTime - processSpineStartTime}ms)\n" +
                "\tnav-doc time: ${processNavigationDocumentEndTime - processNavigationDocumentStartTime}ms \n"
    }

}