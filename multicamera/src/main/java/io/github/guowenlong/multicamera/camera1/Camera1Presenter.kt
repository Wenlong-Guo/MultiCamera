package io.github.guowenlong.multicamera.camera1

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceView
import io.github.guowenlong.multicamera.bean.CameraLensFacing
import io.github.guowenlong.multicamera.bean.MultiSize
import io.github.guowenlong.multicamera.core.ICamera
import io.github.guowenlong.multicamera.utils.CameraUtils
import java.io.IOException

/**
 * Description: Camera的代理类
 * Author:      郭文龙
 * Date:        2022/4/22 20:21
 * Gmail:       guowenlong20000@sina.com
 */
class Camera1Presenter(private val surfaceView: SurfaceView) : ICamera {

    companion object {
        const val TAG = "CameraPresenter"
    }

    var size = MultiSize(1080, 1920)

    private var camera: Camera? = null

    override fun releaseCamera() {
        Log.e("guowenlong", "releaseCamera")
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    override fun switchCamera(cameraLensFacing: CameraLensFacing) {
        releaseCamera()
        openCamera(cameraLensFacing)
    }

    override fun getMaxZoom(): Int {
        return camera?.parameters?.maxZoom ?: 0
    }

    override fun setZoom(zoom: Int) {
        camera?.parameters?.let { parameters ->
            parameters.zoom = zoom
            camera?.parameters = parameters
        } ?: run {
            Log.e(TAG, "setZoom parameters is null")
        }
    }

    override fun getMultiSize() = size

    override fun takePicture(
        shutterCallback: Camera.ShutterCallback?,
        raw: Camera.PictureCallback?,
        jpeg: Camera.PictureCallback
    ) {
        camera?.takePicture(shutterCallback, raw, jpeg)
    }

    override fun openCamera(cameraLensFacing: CameraLensFacing, size: MultiSize?) {
        try {
            camera = Camera.open(cameraLensFacing.camera1)
            val parameters = camera?.parameters
            if (parameters?.supportedFocusModes?.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) == true) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            } else {
                parameters?.supportedFocusModes?.get(0)?.let {
                    parameters.focusMode = it
                }
            }

            CameraUtils.getBestSize(
                parameters?.supportedPreviewSizes ?: listOf(),
                this.size.previewWidth,
                this.size.previewHeight
            ).let {
                parameters?.setPreviewSize(it.width, it.height)
                size?.coverPreviewSize(it.width, it.height)
            }
            CameraUtils.getBestSize(
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

    override fun startPreview(surfaceTexture: SurfaceTexture?) {
        Log.e("guowenlong", "startPreview")
        try {
            if (surfaceTexture == null) {
                camera?.startPreview()
            } else {
                camera?.setPreviewTexture(surfaceTexture)
                camera?.startPreview()
            }
        } catch (e: IOException) {
            Log.e(TAG, "startPreview", e)
        }
    }

    override fun stopPreview() {
        Log.e("guowenlong", "stopPreview")
        camera?.stopPreview()
    }
}