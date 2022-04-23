package io.github.guowenlong.camera.adapter

import android.app.Activity
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.opengl.GLES20
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import io.github.guowenlong.camera.bean.CameraConfig
import io.github.guowenlong.camera.core.ICameraPresenter
import java.io.IOException

/**
 * Description: Camera的代理类
 * Author:      郭文龙
 * Date:        2022/4/22 20:21
 * Gmail:       guowenlong20000@sina.com
 */
class CameraPresenter : ICameraPresenter {
    companion object {
        const val TAG = "CameraPresenter"
    }

    private lateinit var surfaceView: SurfaceView
    private var cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
    private var camera: Camera? = null
    private var preWidth : Int = 0;
    private var preHeight : Int = 0;
    private var config: CameraConfig =
        CameraConfig(2.16F, 1000, Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)

    override fun bindSurfaceView(surfaceView: SurfaceView) {
        this.surfaceView = surfaceView
    }

    override fun setConfig(config: CameraConfig) {
        this.config = config
    }

    override fun openCamera(cameraId: Int) {
        try {
            this.cameraId = cameraId
            camera = Camera.open(cameraId)
            val parameters = camera?.parameters
            if (parameters?.supportedFocusModes?.contains(config.focusMode) == true) {
                parameters.focusMode = config.focusMode
            }

            getBestSize(
                parameters?.supportedPreviewSizes ?: listOf(),
                surfaceView.width,
                surfaceView.height
            ).let {
                parameters?.setPreviewSize(1280, 720)
                preWidth = 1280
                preHeight = 720
            }
            getBestSize(
                parameters?.supportedPictureSizes ?: listOf(),
                surfaceView.width,
                surfaceView.height
            ).let {
                parameters?.setPictureSize(it.width, it.height)
            }
            camera?.parameters = parameters
            setCameraDisplayOrientation(cameraId)
        } catch (e: Exception) {
            Log.e(TAG, "openCamera", e)
        }
    }

    override fun getPreWidth(): Int {
        return preWidth
    }

    override fun getPreHeight(): Int {
        return preHeight
    }

    override fun releaseCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    override fun switchCamera(cameraId: Int?, surfaceTextTure: SurfaceTexture) {
        this.cameraId = cameraId
            ?: if (this.cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Camera.CameraInfo.CAMERA_FACING_BACK
            } else {
                Camera.CameraInfo.CAMERA_FACING_FRONT
            }
        releaseCamera()
        openCamera(this.cameraId)
        startPreview(surfaceTextTure)
    }

    override fun startPreview(surfaceTextTure: SurfaceTexture) {
        try {
            camera?.setPreviewTexture(surfaceTextTure)
            camera?.startPreview()
        } catch (e: IOException) {
            Log.e(TAG, "startPreview", e)
        }
    }

    override fun stopPreview() {
        camera?.stopPreview()
    }

    override fun takePicture(
        shutter: Camera.ShutterCallback?,
        raw: PictureCallback?,
        jpeg: PictureCallback
    ) {
        camera?.takePicture(shutter, raw, jpeg)
    }

    private fun setCameraDisplayOrientation(cameraId: Int) {
        val targetActivity = surfaceView.context as Activity
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = targetActivity.windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera?.setDisplayOrientation(result)
    }
}