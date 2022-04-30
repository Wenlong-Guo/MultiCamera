//package io.github.guowenlong.multicamera.camera2
//
//import android.app.Activity
//import android.content.Context
//import android.graphics.ImageFormat
//import android.graphics.SurfaceTexture
//import android.hardware.camera2.*
//import android.media.ImageReader
//import android.os.Handler
//import android.os.HandlerThread
//import android.util.Log
//import io.github.guowenlong.multicamera.temp.Camera2Proxy
//import java.util.*
//
///**
// * Description: Camera2 的 代理类
// * Author:      郭文龙
// * Date:        2022/4/30 13:23
// * Gmail:       guowenlong20000@sina.com
// */
//class Camera2Presenter(context: Context) {
//    companion object {
//        private const val TAG = "Camera2 Presenter"
//    }
//
//    private val cameraManager: CameraManager =
//        (context as Activity).getSystemService(Context.CAMERA_SERVICE) as CameraManager
//
//    // 相机对象
//    private var cameraDevice: CameraDevice? = null
//
//    //相机的HandlerThread
//    private var backgroundThread: HandlerThread = HandlerThread("CameraBackground")
//    private var backgroundHandler: Handler = Handler(backgroundThread.looper)
//    private var mPreviewRequestBuilder // 相机预览请求的构造器
//            : CaptureRequest.Builder? = null
//    private var captureSession: CameraCaptureSession? = null
//
//    /**
//     * 打开摄像头的回调
//     */
//    private val cameraDeviceStateCallback by lazy {
//        object : CameraDevice.StateCallback() {
//            override fun onOpened(camera: CameraDevice) {
//                Log.d(TAG, "onOpened")
//                cameraDevice = camera
//                initPreviewRequest()
//            }
//
//            override fun onDisconnected(camera: CameraDevice) {
//                Log.d(TAG, "onDisconnected")
//                releaseCamera()
//            }
//
//            override fun onError(camera: CameraDevice, error: Int) {
//                Log.e(TAG, "Camera Open failed, error: $error")
//                releaseCamera()
//            }
//        }
//    }
//
//    fun openCamera(width: Int, height: Int) {
//        Log.v(TAG, "openCamera")
//        startBackgroundThread()
//        try {
//            mCameraCharacteristics =
//                mCameraManager.getCameraCharacteristics(Integer.toString(mCameraId))
//            val map =
//                mCameraCharacteristics!!.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
//            // 拍照大小，选择能支持的一个最大的图片大小
//            val largest = Collections.max(
//                Arrays.asList(*map!!.getOutputSizes(ImageFormat.JPEG)),
//                Camera2Proxy.CompareSizesByArea()
//            )
//            Log.d(TAG, "picture size: " + largest.width + "*" + largest.height)
//            mImageReader =
//                ImageReader.newInstance(largest.width, largest.height, ImageFormat.JPEG, 2)
//            // 预览大小，根据上面选择的拍照图片的长宽比，选择一个和控件长宽差不多的大小
//            mPreviewSize = chooseOptimalSize(
//                map.getOutputSizes(SurfaceTexture::class.java),
//                width,
//                height,
//                largest
//            )
//            Log.d(
//                Camera2Proxy.TAG,
//                "preview size: " + mPreviewSize!!.width + "*" + mPreviewSize!!.height
//            )
//            // 打开摄像头
//            mCameraManager.openCamera(
//                Integer.toString(mCameraId),
//                mStateCallback,
//                mBackgroundHandler
//            )
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun initPreviewRequest() {
//        try {
//            mPreviewRequestBuilder =
//                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
//            mPreviewRequestBuilder!!.addTarget(mPreviewSurface!!) // 设置预览输出的 Surface
//            mCameraDevice!!.createCaptureSession(
//                Arrays.asList(mPreviewSurface, mImageReader!!.surface),
//                object : CameraCaptureSession.StateCallback() {
//                    override fun onConfigured(session: CameraCaptureSession) {
//                        mCaptureSession = session
//                        // 设置连续自动对焦
//                        mPreviewRequestBuilder!!.set(
//                            CaptureRequest.CONTROL_AF_MODE,
//                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
//                        )
//                        // 设置自动曝光
//                        mPreviewRequestBuilder!!.set(
//                            CaptureRequest.CONTROL_AE_MODE,
//                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
//                        )
//                        // 设置完后自动开始预览
//                        mPreviewRequest = mPreviewRequestBuilder!!.build()
//                        startPreview()
//                    }
//
//                    override fun onConfigureFailed(session: CameraCaptureSession) {
//                        Log.e(TAG, "ConfigureFailed. session: mCaptureSession")
//                    }
//                }, backgroundHandler
//            ) // handle 传入 null 表示使用当前线程的 Looper
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    }
//
//    fun releaseCamera() {
//        Log.v(TAG, "releaseCamera")
//        if (null != mCaptureSession) {
//            mCaptureSession!!.close()
//            mCaptureSession = null
//        }
//        if (cameraDevice != null) {
//            cameraDevice?.close()
//            cameraDevice = null
//        }
//        if (mImageReader != null) {
//            mImageReader!!.close()
//            mImageReader = null
//        }
//        stopBackgroundThread()
//    }
//
//    private fun startBackgroundThread() {
//        Log.v(TAG, "startBackgroundThread")
//        backgroundThread.start()
//    }
//}