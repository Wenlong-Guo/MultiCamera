package io.github.guowenlong.camera.utils

import android.graphics.Bitmap
import android.opengl.GLException
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * Description: GLSurfaceView 的工具类
 * Author:      郭文龙
 * Date:        2022/4/27 17:45
 * Gmail:       guowenlong20000@sina.com
 */
object GLSurfaceViewUtils {
    fun createBitmapFromGLSurface(x: Int, y: Int, w: Int, h: Int, gl: GL10?): Bitmap? {
        val bitmapBuffer = IntArray(w * h)
        val bitmapSource = IntArray(w * h)
        val intBuffer: IntBuffer = IntBuffer.wrap(bitmapBuffer)
        intBuffer.position(0)
        try {
            gl?.glReadPixels(
                x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                intBuffer
            )
            var offset1: Int
            var offset2: Int
            for (i in 0 until h) {
                offset1 = i * w
                offset2 = (h - i - 1) * w
                for (j in 0 until w) {
                    val texturePixel = bitmapBuffer[offset1 + j]
                    val blue = texturePixel shr 16 and 0xff
                    val red = texturePixel shl 16 and 0x00ff0000
                    val pixel = texturePixel and -0xff0100 or red or blue
                    bitmapSource[offset2 + j] = pixel
                }
            }
        } catch (e: GLException) {
            return null
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)
    }
}