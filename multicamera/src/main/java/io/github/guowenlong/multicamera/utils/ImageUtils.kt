package io.github.guowenlong.multicamera.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix


/**
 * Description: 图片相关工具类
 * Author:      郭文龙
 * Date:        2022/4/27 16:10
 * Gmail:       guowenlong20000@sina.com
 */
object ImageUtils {
    fun bytes2Bitmap(bytes: ByteArray): Bitmap? {
        return if (bytes.isEmpty()) null else BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )
    }

    fun rotateBitmap(angle: Int, bitmap: Bitmap): Bitmap? {
        var returnBm: Bitmap? = null
        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(-angle.toFloat())
        try {
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        if (returnBm == null) returnBm = bitmap
        if (bitmap != returnBm) bitmap.recycle()
        return returnBm
    }
}