package io.github.guowenlong.multicamera.utils

import android.graphics.*
import android.hardware.Camera
import android.util.Log
import kotlin.math.abs

/**
 * Description: 相机工具类
 * Author:      郭文龙
 * Date:        2022/5/12 22:44
 * Gmail:       guowenlong20000@sina.com
 */
object CameraUtils {

    fun getBestSize(
        sizes: List<Camera.Size>,
        currentWidth: Int,
        currentHeight: Int
    ): Camera.Size {
        var i = 1
        var bestIndex = 0
        //大头
        var bestWidth: Int = sizes[0].width
        //小头
        var bestHeight: Int = sizes[0].height

        var min =
            abs(bestHeight.toFloat() / bestWidth.toFloat() - currentWidth.toFloat() / currentHeight.toFloat())
        while (i < sizes.size) {
            val current =
                abs(sizes[i].height.toFloat() / sizes[i].width.toFloat() - currentWidth.toFloat() / currentHeight.toFloat())
            if (current < min) {
                min = current
                bestWidth = sizes[i].width
                bestHeight = sizes[i].height
                bestIndex = i

                Log.d("glcamera + $i 个", "$bestWidth//$bestHeight")
            }
            Log.d("glcamera + $i 个", "${sizes[i].width.toFloat() / sizes[i].height}")
            i++
        }

        val result = IntArray(2)
        result[0] = bestWidth
        result[1] = bestHeight

        Log.v("glcamera", "${sizes[bestIndex].width}//${sizes[bestIndex].height}")

        return sizes[bestIndex]
    }

    /**
     *
     * 选择变换
     *
     * @param origin 原图
     * @param alpha 旋转角度，可正可负
     * @return 旋转后的图片
     */
    fun rotateBitmap(origin: Bitmap, alpha: Float): Bitmap {
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.setRotate(alpha)

        // 围绕原地进行旋转
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
        origin.recycle()
        return newBM
    }

    fun flip(bitmap: Bitmap, isX: Boolean, isY: Boolean): Bitmap {
        val w: Int = bitmap.width
        val h: Int = bitmap.height
        val newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888) // 创建一个新的和SRC长度宽度一样的位图

        val cv = Canvas(newb)
        val m = android.graphics.Matrix()
        if (isY) m.postScale(1f, -1f) //镜像垂直翻转
        if (isX) m.postScale(-1f, 1f) //镜像水平翻转
        val new2: Bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, true)
        cv.drawBitmap(new2, Rect(0, 0, new2.width, new2.height), Rect(0, 0, w, h), null)
        bitmap.recycle()
        return newb
    }
}