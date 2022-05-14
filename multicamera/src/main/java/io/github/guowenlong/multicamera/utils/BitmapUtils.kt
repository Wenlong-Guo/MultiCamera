package io.github.guowenlong.multicamera.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Description: Bitmap工具类
 * Author:      郭文龙
 * Date:        2022/5/13 23:02
 * Gmail:       guowenlong20000@sina.com
 */
object BitmapUtils {
    fun bytesToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}