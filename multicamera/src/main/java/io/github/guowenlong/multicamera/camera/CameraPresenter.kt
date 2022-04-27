package io.github.guowenlong.multicamera.camera

import android.app.Activity
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import io.github.guowenlong.multicamera.bean.CameraConfig
import io.github.guowenlong.multicamera.bean.MultiSize
import java.io.IOException
import kotlin.math.abs

/**
 * Description: Camera的代理类
 * Author:      郭文龙
 * Date:        2022/4/22 20:21
 * Gmail:       guowenlong20000@sina.com
 */
class CameraPresenter(private val surfaceView: SurfaceView) {

    companion object {
        const val TAG = "CameraPresenter"
    }

    var cameraConfig = CameraConfig()
    var cameraSize = MultiSize(1920, 1080)

    private var camera: Camera? = null

    fun openCamera(cameraId: Int) {
        try {
            cameraConfig.cameraId = cameraId
            camera = Camera.open(cameraId)
            val parameters = camera?.parameters
            if (parameters?.supportedFocusModes?.contains(cameraConfig.focusMode) == true) {
                parameters.focusMode = cameraConfig.focusMode
            } else {
                parameters?.supportedFocusModes?.get(0)?.let {
                    parameters.focusMode = it
                }
            }

            getBestSize(
                parameters?.supportedPreviewSizes ?: listOf(),
                surfaceView.width,
                surfaceView.height
            ).let {
                parameters?.setPreviewSize(it.width, it.height)
                cameraSize.cover(it.width, it.height)
            }
            getBestSize(
                parameters?.supportedPictureSizes ?: listOf(),
                surfaceView.width,
                surfaceView.height
            ).let {
                parameters?.setPictureSize(it.width, it.height)
            }
            camera?.parameters = parameters
        } catch (e: Exception) {
            Log.e(TAG, "openCamera", e)
        }
    }

    fun releaseCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    fun switchCamera(cameraId: Int) {
        releaseCamera()
        openCamera(cameraId)
    }

    fun startPreview(surfaceTextTure: SurfaceTexture) {
        try {
            camera?.setPreviewTexture(surfaceTextTure)
            camera?.startPreview()
        } catch (e: IOException) {
            Log.e(TAG, "startPreview", e)
        }
    }

    fun stopPreview() {
        camera?.stopPreview()
    }

    private fun getBestSize(
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