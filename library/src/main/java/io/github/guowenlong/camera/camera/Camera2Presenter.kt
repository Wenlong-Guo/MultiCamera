package io.github.guowenlong.camera.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import io.github.guowenlong.camera.bean.CameraLensFacing
import io.github.guowenlong.camera.bean.MultiSize
import io.github.guowenlong.camera.core.ICamera
import java.util.*

/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/5/6 22:16
 * Gmail:       guowenlong20000@sina.com
 */
class Camera2Presenter(private val activity: Activity) : ICamera {

    companion object {
        private const val TAG = "Camera2Presenter"
    }

    private val cameraManager: CameraManager =
        activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private var surfaceTexture: SurfaceTexture? = null

    private val size = MultiSize()

    private var lensFacing: CameraLensFacing = CameraLensFacing.FRONT

    private var mBackgroundHandler: Handler? = null

    private var mBackgroundThread: HandlerThread? = null

    private lateinit var mCameraId: String

    private lateinit var mCameraCharacteristics: CameraCharacteristics

    private var cameraDevice: CameraDevice? = null

    private var mPreviewSurface: Surface? = null

    private var mCaptureSession: CameraCaptureSession? = null

    /**
     * 打开摄像头的回调
     */
    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "onOpened")
            cameraDevice = camera
            createCaptureSession(camera)
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "onDisconnected")
            releaseCamera()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e(TAG, "Camera Open failed, error: $error")
            releaseCamera()
        }
    }

    private val mCaptureCallBack = object : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureFailed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            failure: CaptureFailure
        ) {
            super.onCaptureFailed(session, request, failure)
            Log.e(TAG, "onCaptureFailed 开启预览失败! ")
        }
    }

    override fun bindSurfaceView(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
        surfaceTexture.setDefaultBufferSize(1920, 1080)

    }

    @SuppressLint("MissingPermission")
    override fun openCamera(cameraLensFacing: CameraLensFacing?, size: MultiSize?) {
        Log.v(TAG, "openCamera")
        startBackgroundThread() // 对应 releaseCamera() 方法中的 stopBackgroundThread()
        findCameraFacing(cameraLensFacing?.camera2 ?: lensFacing.camera2)
        //todo 预览尺寸
        try {
            // 打开摄像头
            cameraManager.openCamera(mCameraId, mStateCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun createCaptureSession(cameraDevice : CameraDevice) {
        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        this.mPreviewSurface = Surface(surfaceTexture)
        captureRequestBuilder.addTarget(mPreviewSurface!!)  // 将CaptureRequest的构建器与Surface对象绑定在一起
        captureRequestBuilder.set(
            CaptureRequest.CONTROL_AE_MODE,
            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
        )      // 闪光灯
        captureRequestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        ) // 自动对焦

        // 为相机预览，创建一个CameraCaptureSession对象
        cameraDevice.createCaptureSession(
            listOf(mPreviewSurface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "开启预览会话失败！")
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    mCaptureSession = session
                    session.setRepeatingRequest(
                        captureRequestBuilder.build(),
                        mCaptureCallBack,
                        mBackgroundHandler
                    )
                }

            },
            mBackgroundHandler
        )
    }

    override fun startPreview() {

    }

    override fun stopPreview() {

    }

    override fun releaseCamera() {
        mCaptureSession?.close()
        mCaptureSession = null

        cameraDevice?.close()
        cameraDevice = null
    }

    override fun switchCamera(cameraLensFacing: CameraLensFacing) {

    }

    override fun getMaxZoom(): Int {
        return 100
    }

    override fun setZoom(zoom: Int) {

    }

    private fun startBackgroundThread() {
        if (mBackgroundThread == null || mBackgroundHandler == null) {
            Log.v(TAG, "startBackgroundThread")
            mBackgroundThread = HandlerThread("CameraBackground")
            mBackgroundThread?.start()
            mBackgroundHandler = Handler(mBackgroundThread!!.looper)
        }
    }

    private fun stopBackgroundThread() {
        Log.v(TAG, "stopBackgroundThread")
        if (mBackgroundThread != null) {
            mBackgroundThread!!.quitSafely()
            try {
                mBackgroundThread!!.join()
                mBackgroundThread = null
                mBackgroundHandler = null
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun findCameraFacing(targetFacing: Int) {
        val cameraIdList = cameraManager.cameraIdList
        if (cameraIdList.isEmpty()) {
            Log.e(TAG, "没有可用相机")
            return
        }
        for (id in cameraIdList) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)
            val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)

            if (facing == targetFacing) {
                mCameraId = id
                mCameraCharacteristics = cameraCharacteristics
            }
            Log.d(TAG, "设备中的摄像头 $id")
        }
    }
}