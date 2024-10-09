package cn.li.baselib.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.core.graphics.createBitmap

/**
 *
 *
 * @author Grimrise 2024/9/29
 */
object ScreenShotUtils {
    private const val ORIGIN_PIXEL_COLOR = -1

    private const val SCALE = 0.1f


    fun viewToBitmap(view: View, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, config)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    // 预览图
    fun fastShot(view: View, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
        val width = (view.width * SCALE + 0.5f).takeIf { it > 0 }?.toInt() ?: view.width
        val height = (view.height * SCALE + 0.5f).takeIf { it > 0 }?.toInt() ?: view.height
        val bitmap = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(bitmap)
        view.computeScroll()
        canvas.scale(SCALE, SCALE)
        canvas.translate(-view.scrollX.toFloat(), -view.scrollY.toFloat())
        view.draw(canvas)
        return bitmap
    }

    fun screenShot(window: Window, config: Bitmap.Config = Bitmap.Config.ARGB_8888, callback: (bitmap: Bitmap?) -> Unit) {
        val root = window.peekDecorView()
        val bitmap = createBitmap(root.width, root.height, config)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PixelCopy.request(
                window,
                bitmap,
                PixelCopy.OnPixelCopyFinishedListener { status ->
                    if (status ==PixelCopy.SUCCESS) {
                        callback(bitmap)
                    } else {
                        bitmap.recycle()
                        callback(null)
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } else {
            root.draw(Canvas(bitmap))
            callback(bitmap)
        }
    }


    /**
     * 检测白屏（不一定是白色）
     * @param bitmap 待检测的bitmap
     * @param originPix 目标颜色 [android.graphics.Color]，默认使用bitmap的第一个像素值
     * */
    fun checkBlank(bitmap: Bitmap, originPix: Int = ORIGIN_PIXEL_COLOR): CheckResult {
        if (bitmap.isRecycled) {
            return CheckResult(checkStatus = false, failedReason = "bitmap is recycled")
        }
        val startTime = SystemClock.elapsedRealtime()
        val fixOriginPix = if (originPix == ORIGIN_PIXEL_COLOR) {
            bitmap.getPixel(0, 0)
        } else {
            originPix
        }
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width)
        var validatePixels = 0
        for (i in 0 ..< height) {
            bitmap.getPixels(pixels, 0, width, 0, i, width, 1)
            for (j in 0 ..< width) {
                if (pixels[j] == fixOriginPix) {
                    continue
                }
                validatePixels++
            }
        }
        return CheckResult(
            validateRate = validatePixels.toFloat() / (width * height),
            checkStatus = true,
            costTime = SystemClock.elapsedRealtime() - startTime,
        )
    }

    data class CheckResult(
        val validateRate: Float = -1f, // 有效像素的比例
        val checkStatus: Boolean = false, // 检测是否成功
        val costTime: Long = -1L, // 检测耗时
        val failedReason: String? = null // 失败原因
    )

}