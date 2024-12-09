package cn.li.baselib.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.view.WindowManager
import cn.li.baselib.ext.dp
import cn.li.baselib.ext.dpInt
import java.io.BufferedReader
import java.io.FileReader

/**
 * 设备信息
 * TODO: 各个国内品牌的判断
 * @author Grimrise 2024/09/29
 * */
@SuppressLint
object DeviceUtils {

    /**
     * 系统版本
     * */
    var osSdk: Int = -1
        get() {
            if (field == -1) {
                field = Build.VERSION.SDK_INT
            }
            return field
        }
        private set

    /**
     * 系统品牌, 比如 OPPO, Google
     * */
    var osBrand: String = ""
        get() {
            if (field.isEmpty()) {
                field = Build.BRAND
            }
            return field
        }
        private set

    /**
     * 系统型号
     * */
    var osModel: String = ""
        get() {
            if (field.isEmpty()) {
                field = Build.MODEL
            }
            return field
        }
        private set


    /**
     * 屏幕的宽高，x宽，y高
     * */
    fun getScreenSize(context: Context): Point {
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealSize(point) ?: run {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.defaultDisplay.getSize(point)
            }
        } else {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getSize(point)
        }
        return point
    }

    private var statusBarHeight: Int = -1
    /**
     * 状态栏高度, px
     * */
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    fun getStatusBarHeight(context: Context): Int {
        if (statusBarHeight != -1) {
            return statusBarHeight
        }
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        if (result == 0) {
            result = 25.dpInt
        }
        statusBarHeight = result
        return result
    }

    fun checkStatusBarVisible(activity: Activity): Boolean {
        val decorView = activity.window.decorView
        val rect = Rect()
        // 该方法获取可见区域，不包括上下的状态栏
        decorView.getWindowVisibleDisplayFrame(rect)
        return rect.top > decorView.top
    }

    private var navigationBarHeight: Int = -1
    /**
     * 导航栏高度， px
     * */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getNavigationBarHeight(context: Context): Int {
        if (navigationBarHeight != -1) {
            return navigationBarHeight
        }
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")

        navigationBarHeight = if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
        return navigationBarHeight
    }


    /**
     * 系统总内存，byte
     * */
    var totalMemory: Long = 0L
        get() {
            // 读取 /proc/meminfo 第一行
            // MemTotal:        2025912 kB
            // MemFree:          248084 kB
            // MemAvailable:     712068 kB
            if (field == 0L) {
                try {
                    val reader = BufferedReader(FileReader("/proc/meminfo"))
                    field = reader.readLine().split("\\s+".toRegex())[1].toLong() * 1024
                } catch (e: Exception) {
                    //
                }
            }
            return field
        }
        private set

    /**
     * 可用存储空间，byte
     * */
    val availableRomSize: Long
        get() {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            return availableBlocks * blockSize
        }


    /**
     * 总存储空间，byte
     * */
    val totalRomSize: Long
        get() {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            return totalBlocks * blockSize
        }


    var cpuApi: String = ""
        get() {
            if (field.isNotEmpty()) {
                return field
            }
            val result = if (Build.SUPPORTED_ABIS.isNotEmpty()) {
                Build.SUPPORTED_ABIS.joinToString()
            } else {
                Build.CPU_ABI
            }
            if (result.isEmpty()) {
                return Build.UNKNOWN
            }
            field = result
            return result
        }
        private set
}