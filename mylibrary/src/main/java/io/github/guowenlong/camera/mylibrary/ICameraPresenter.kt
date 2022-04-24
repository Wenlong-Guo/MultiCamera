package io.github.guowenlong.camera.mylibrary

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceView
import kotlin.math.abs

/**
 * Description: 相机的代理类
 * Author:      郭文龙
 * Date:        2022/4/22 19:50
 * Gmail:       guowenlong20000@sina.com
 */
interface ICameraPresenter {
    fun bindSurfaceView(surfaceView: SurfaceView)
    fun setConfig(config: CameraConfig)
    fun openCamera(cameraId: Int)
    fun getPreWidth():Int
    fun getPreHeight():Int
    fun releaseCamera()
    fun switchCamera(cameraId: Int? = null, surfaceTextTure: SurfaceTexture)
    fun startPreview(surfaceTextTure: SurfaceTexture)
    fun stopPreview()
    fun takePicture(
        shutter: Camera.ShutterCallback? = null,
        raw: Camera.PictureCallback? = null,
        jpeg: Camera.PictureCallback
    )

    fun getBestSize(sizes: List<Camera.Size>, currentWidth: Int, currentHeight: Int): Camera.Size {
        var i = 1
        var bestIndex = 0
        //大头
        var bestWidth: Int = sizes[0].width
        //小头
        var bestHeight: Int = sizes[0].height

        var min =
            abs(bestHeight.toFloat() / bestWidth.toFloat() - currentWidth.toFloat() / currentHeight.toFloat())
        while (i < sizes.size) {
            val current = abs(sizes[i].height.toFloat() / sizes[i].width.toFloat() - currentWidth.toFloat() / currentHeight.toFloat())
            if (current < min) {
                min = current
                bestWidth = sizes[i].width
                bestHeight = sizes[i].height
                bestIndex = i

                Log.d("glcamera + $i 个", "$bestWidth//$bestHeight")
            }
            Log.d("glcamera + $i 个", "${sizes[i].width.toFloat()/sizes[i].height}")
            i++
        }

        val result = IntArray(2)
        result[0] = bestWidth
        result[1] = bestHeight

        Log.v("glcamera", "${sizes[bestIndex].width}//${sizes[bestIndex].height}")

        return sizes[bestIndex]
    }
}