package io.github.guowenlong.multicamera.utils

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
}