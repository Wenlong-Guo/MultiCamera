package io.github.guowenlong.camera.camera

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.params.MeteringRectangle
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.SurfaceHolder
import java.util.*


/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/4/30 1:24
 * Gmail:       guowenlong20000@sina.com
 */


class Camera2Proxy @TargetApi(Build.VERSION_CODES.M) constructor(activity: Activity) {
    private var mCameraId: Int = CameraCharacteristics.LENS_FACING_FRONT
    private var mPreviewSize // 预览大小
            : Size? = null
    private val mCameraManager // 相机管理者
            : CameraManager
    private var mCameraCharacteristics // 相机属性
            : CameraCharacteristics? = null
    private var mCameraDevice // 相机对象
            : CameraDevice? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mPreviewRequestBuilder // 相机预览请求的构造器
            : CaptureRequest.Builder? = null
    private var mPreviewRequest: CaptureRequest? = null
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null
    private var mImageReader: ImageReader? = null
    private var mPreviewSurface: Surface? = null
    private var mDisplayRotate = 0
    private var mZoom = 0 // 缩放

    companion object {
        private const val TAG = "Camera2Proxy"
    }

    init {
        mCameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /**
     * 打开摄像头的回调
     */
    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "onOpened")
            mCameraDevice = camera
            initPreviewRequest()
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

    @SuppressLint("MissingPermission")
    fun openCamera(cameraId: Int) {
        Log.v(TAG, "openCamera")
        startBackgroundThread() // 对应 releaseCamera() 方法中的 stopBackgroundThread()
        try {
            mCameraCharacteristics =
                mCameraManager.getCameraCharacteristics(getTargetCameraId(mCameraId))
            val map =
                mCameraCharacteristics!!.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            // 拍照大小，选择能支持的一个最大的图片大小
            val largest = Collections.max(
                Arrays.asList(*map!!.getOutputSizes(ImageFormat.JPEG)),
                CompareSizesByArea()
            )
            Log.d(TAG, "picture size: " + largest.width + "*" + largest.height)
            mImageReader =
                ImageReader.newInstance(largest.width, largest.height, ImageFormat.JPEG, 2)
            // 预览大小，根据上面选择的拍照图片的长宽比，选择一个和控件长宽差不多的大小
            mPreviewSize = chooseOptimalSize(
                map.getOutputSizes(SurfaceTexture::class.java),
                1080,
                1920,
                largest
            )
            Log.d(TAG, "preview size: " + mPreviewSize!!.width + "*" + mPreviewSize!!.height)
            // 打开摄像头
            mCameraManager.openCamera(
                getTargetCameraId(mCameraId),
                mStateCallback,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun getTargetCameraId(cameraId: Int): String {
        mCameraManager.cameraIdList.map {
            val characteristics: CameraCharacteristics = mCameraManager.getCameraCharacteristics(it)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (facing == cameraId) return it
        }
        return ""
    }

    fun releaseCamera() {
        Log.v(TAG, "releaseCamera")
        if (null != mCaptureSession) {
            mCaptureSession!!.close()
            mCaptureSession = null
        }
        if (mCameraDevice != null) {
            mCameraDevice!!.close()
            mCameraDevice = null
        }
        if (mImageReader != null) {
            mImageReader!!.close()
            mImageReader = null
        }
        stopBackgroundThread() // 对应 openCamera() 方法中的 startBackgroundThread()
    }

    fun setImageAvailableListener(onImageAvailableListener: ImageReader.OnImageAvailableListener?) {
        if (mImageReader == null) {
            Log.w(TAG, "setImageAvailableListener: mImageReader is null")
            return
        }
        mImageReader!!.setOnImageAvailableListener(onImageAvailableListener, null)
    }

    fun setPreviewSurface(holder: SurfaceHolder) {
        mPreviewSurface = holder.surface
    }

    fun setPreviewSurface(surfaceTexture: SurfaceTexture) {
        mPreviewSize?.let {
            surfaceTexture.setDefaultBufferSize(it.width, it.height)
            mPreviewSurface = Surface(surfaceTexture)
        }
    }

    private fun initPreviewRequest() {
        try {
            mPreviewRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder!!.addTarget(mPreviewSurface!!) // 设置预览输出的 Surface
            mCameraDevice!!.createCaptureSession(
                Arrays.asList(mPreviewSurface, mImageReader!!.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        Log.e("guowenlong", "CameraCaptureSession $session")
                        Log.e("guowenlong", "mPreviewRequestBuilder $mPreviewRequestBuilder")
                        mCaptureSession = session
                        // 设置连续自动对焦
                        mPreviewRequestBuilder!!.set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        )
                        // 设置自动曝光
                        mPreviewRequestBuilder!!.set(
                            CaptureRequest.CONTROL_AE_MODE,
                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                        )
                        // 设置完后自动开始预览
                        mPreviewRequest = mPreviewRequestBuilder!!.build()
                        startPreview()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "ConfigureFailed. session: mCaptureSession")
                    }
                }, mBackgroundHandler
            ) // handle 传入 null 表示使用当前线程的 Looper
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun startPreview() {
        Log.v(TAG, "startPreview")
        if (mCaptureSession == null || mPreviewRequestBuilder == null) {
            Log.w(TAG, "startPreview: mCaptureSession or mPreviewRequestBuilder is null")
            return
        }
        try {
            // 开始预览，即一直发送预览的请求
            mCaptureSession!!.setRepeatingRequest(mPreviewRequest!!, null, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun stopPreview() {
        Log.v(TAG, "stopPreview")
        if (mCaptureSession == null || mPreviewRequestBuilder == null) {
            Log.w(TAG, "stopPreview: mCaptureSession $mCaptureSession or mPreviewRequestBuilder $mPreviewRequestBuilder is null")
            return
        }
        try {
            mCaptureSession!!.stopRepeating()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

//    fun captureStillPicture() {
//        try {
//            val captureBuilder =
//                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
//            captureBuilder.addTarget(mImageReader!!.surface)
//            captureBuilder.set(
//                CaptureRequest.CONTROL_AF_MODE,
//                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
//            )
//            captureBuilder.set(
//                CaptureRequest.JPEG_ORIENTATION,
//                getJpegOrientation(mDeviceOrientation)
//            )
//            // 预览如果有放大，拍照的时候也应该保存相同的缩放
//            val zoomRect = mPreviewRequestBuilder!!.get(CaptureRequest.SCALER_CROP_REGION)
//            if (zoomRect != null) {
//                captureBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect)
//            }
//            mCaptureSession!!.stopRepeating()
//            mCaptureSession!!.abortCaptures()
//            val time = System.currentTimeMillis()
//            mCaptureSession!!.capture(captureBuilder.build(), object : CaptureCallback() {
//                override fun onCaptureCompleted(
//                    session: CameraCaptureSession,
//                    request: CaptureRequest,
//                    result: TotalCaptureResult
//                ) {
//                    Log.w(TAG, "onCaptureCompleted, time: " + (System.currentTimeMillis() - time))
//                    try {
//                        mPreviewRequestBuilder!!.set(
//                            CaptureRequest.CONTROL_AF_TRIGGER,
//                            CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
//                        )
//                        mCaptureSession!!.capture(
//                            mPreviewRequestBuilder!!.build(),
//                            null,
//                            mBackgroundHandler
//                        )
//                    } catch (e: CameraAccessException) {
//                        e.printStackTrace()
//                    }
//                    startPreview()
//                }
//            }, mBackgroundHandler)
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    }

    private fun getJpegOrientation(deviceOrientation: Int): Int {
        var deviceOrientation = deviceOrientation
        if (deviceOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) return 0
        val sensorOrientation =
            mCameraCharacteristics!!.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90
        // Reverse device orientation for front-facing cameras
        val facingFront =
            mCameraCharacteristics!!.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
        if (facingFront) deviceOrientation = -deviceOrientation
        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        val jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360
        Log.d(TAG, "jpegOrientation: $jpegOrientation")
        return jpegOrientation
    }

    fun isFrontCamera(): Boolean {
        return mCameraId == CameraCharacteristics.LENS_FACING_BACK
    }

    fun getPreviewSize(): Size? {
        return mPreviewSize
    }

    fun switchCamera(cameraId: Int) {
        mCameraId = mCameraId xor 1
        Log.d(TAG, "switchCamera: mCameraId: $mCameraId")
        releaseCamera()
        openCamera(cameraId)
    }

    private fun chooseOptimalSize(
        sizes: Array<Size>,
        viewWidth: Int,
        viewHeight: Int,
        pictureSize: Size
    ): Size {
        val totalRotation = 90
        val swapRotation = totalRotation == 90 || totalRotation == 270
        val width = if (swapRotation) viewHeight else viewWidth
        val height = if (swapRotation) viewWidth else viewHeight
        return getSuitableSize(sizes, width, height, pictureSize)
    }

    private fun getSuitableSize(
        sizes: Array<Size>,
        width: Int,
        height: Int,
        pictureSize: Size
    ): Size {
        var minDelta = Int.MAX_VALUE // 最小的差值，初始值应该设置大点保证之后的计算中会被重置
        var index = 0 // 最小的差值对应的索引坐标
        val aspectRatio = pictureSize.height * 1.0f / pictureSize.width
        Log.d(TAG, "getSuitableSize. aspectRatio: $aspectRatio")
        for (i in sizes.indices) {
            val size = sizes[i]
            // 先判断比例是否相等
            if (size.width * aspectRatio == size.height.toFloat()) {
                val delta = Math.abs(width - size.width)
                if (delta == 0) {
                    return size
                }
                if (minDelta > delta) {
                    minDelta = delta
                    index = i
                }
            }
        }
        return sizes[index]
    }

//    fun handleZoom(isZoomIn: Boolean) {
//        if (mCameraDevice == null || mCameraCharacteristics == null || mPreviewRequestBuilder == null) {
//            return
//        }
//        // maxZoom 表示 active_rect 宽度除以 crop_rect 宽度的最大值
//        val maxZoom =
//            mCameraCharacteristics!!.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)!!
//        Log.d(TAG, "handleZoom: maxZoom: $maxZoom")
//        val factor = 100 // 放大/缩小的一个因素，设置越大越平滑，相应放大的速度也越慢
//        if (isZoomIn && mZoom < factor) {
//            mZoom++
//        } else if (mZoom > 0) {
//            mZoom--
//        }
//        Log.d(TAG, "handleZoom: mZoom: $mZoom")
//        val rect = mCameraCharacteristics!!.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
//        val minW = ((rect!!.width() - rect.width() / maxZoom) / (2 * factor)).toInt()
//        val minH = ((rect.height() - rect.height() / maxZoom) / (2 * factor)).toInt()
//        val cropW = minW * mZoom
//        val cropH = minH * mZoom
//        Log.d(TAG, "handleZoom: cropW: $cropW, cropH: $cropH")
//        val zoomRect = Rect(cropW, cropH, rect.width() - cropW, rect.height() - cropH)
//        mPreviewRequestBuilder!!.set(CaptureRequest.SCALER_CROP_REGION, zoomRect)
//        mPreviewRequest = mPreviewRequestBuilder!!.build()
//        startPreview() // 需要重新 start preview 才能生效
//    }

    fun focusOnPoint(x: Double, y: Double, width: Int, height: Int) {
        var x = x
        var y = y
        if (mCameraDevice == null || mPreviewRequestBuilder == null) {
            return
        }
        // 1. 先取相对于view上面的坐标
        var previewWidth = mPreviewSize!!.width
        var previewHeight = mPreviewSize!!.height
        if (mDisplayRotate == 90 || mDisplayRotate == 270) {
            previewWidth = mPreviewSize!!.height
            previewHeight = mPreviewSize!!.width
        }
        // 2. 计算摄像头取出的图像相对于view放大了多少，以及有多少偏移
        val tmp: Double
        var imgScale: Double
        var verticalOffset = 0.0
        var horizontalOffset = 0.0
        if (previewHeight * width > previewWidth * height) {
            imgScale = width * 1.0 / previewWidth
            verticalOffset = (previewHeight - height / imgScale) / 2
        } else {
            imgScale = height * 1.0 / previewHeight
            horizontalOffset = (previewWidth - width / imgScale) / 2
        }
        // 3. 将点击的坐标转换为图像上的坐标
        x = x / imgScale + horizontalOffset
        y = y / imgScale + verticalOffset
        if (90 == mDisplayRotate) {
            tmp = x
            x = y
            y = mPreviewSize!!.height - tmp
        } else if (270 == mDisplayRotate) {
            tmp = x
            x = mPreviewSize!!.width - y
            y = tmp
        }
        // 4. 计算取到的图像相对于裁剪区域的缩放系数，以及位移
        var cropRegion = mPreviewRequestBuilder!!.get(CaptureRequest.SCALER_CROP_REGION)
        if (cropRegion == null) {
            Log.w(TAG, "can't get crop region")
            cropRegion =
                mCameraCharacteristics!!.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        }
        val cropWidth = cropRegion!!.width()
        val cropHeight = cropRegion.height()
        if (mPreviewSize!!.height * cropWidth > mPreviewSize!!.width * cropHeight) {
            imgScale = cropHeight * 1.0 / mPreviewSize!!.height
            verticalOffset = 0.0
            horizontalOffset = (cropWidth - imgScale * mPreviewSize!!.width) / 2
        } else {
            imgScale = cropWidth * 1.0 / mPreviewSize!!.width
            horizontalOffset = 0.0
            verticalOffset = (cropHeight - imgScale * mPreviewSize!!.height) / 2
        }
        // 5. 将点击区域相对于图像的坐标，转化为相对于成像区域的坐标
        x = x * imgScale + horizontalOffset + cropRegion.left
        y = y * imgScale + verticalOffset + cropRegion.top
        val tapAreaRatio = 0.1
        val rect = Rect()
        rect.left =
            clamp((x - tapAreaRatio / 2 * cropRegion.width()).toInt(), 0, cropRegion.width())
        rect.right =
            clamp((x + tapAreaRatio / 2 * cropRegion.width()).toInt(), 0, cropRegion.width())
        rect.top =
            clamp((y - tapAreaRatio / 2 * cropRegion.height()).toInt(), 0, cropRegion.height())
        rect.bottom =
            clamp((y + tapAreaRatio / 2 * cropRegion.height()).toInt(), 0, cropRegion.height())
        // 6. 设置 AF、AE 的测光区域，即上述得到的 rect
        mPreviewRequestBuilder!!.set(
            CaptureRequest.CONTROL_AF_REGIONS,
            arrayOf(MeteringRectangle(rect, 1000))
        )
        mPreviewRequestBuilder!!.set(
            CaptureRequest.CONTROL_AE_REGIONS,
            arrayOf(MeteringRectangle(rect, 1000))
        )
        mPreviewRequestBuilder!!.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_AUTO
        )
        mPreviewRequestBuilder!!.set(
            CaptureRequest.CONTROL_AF_TRIGGER,
            CameraMetadata.CONTROL_AF_TRIGGER_START
        )
        mPreviewRequestBuilder!!.set(
            CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
            CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START
        )
        try {
            // 7. 发送上述设置的对焦请求，并监听回调
            mCaptureSession!!.capture(
                mPreviewRequestBuilder!!.build(),
                mAfCaptureCallback,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val mAfCaptureCallback: CaptureCallback = object : CaptureCallback() {
        private fun process(result: CaptureResult) {
            val state = result.get(CaptureResult.CONTROL_AF_STATE) ?: return
            Log.d(TAG, "process: CONTROL_AF_STATE: $state")
            if (state == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || state == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                Log.d(TAG, "process: start normal preview")
                mPreviewRequestBuilder!!.set(
                    CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
                )
                mPreviewRequestBuilder!!.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
                mPreviewRequestBuilder!!.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.FLASH_MODE_OFF
                )
                startPreview()
            }
        }

        override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
        ) {
            process(partialResult)
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            process(result)
        }
    }

    private fun startBackgroundThread() {
        if (mBackgroundThread == null || mBackgroundHandler == null) {
            Log.v(TAG, "startBackgroundThread")
            mBackgroundThread = HandlerThread("CameraBackground")
            mBackgroundThread!!.start()
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

    private fun clamp(x: Int, min: Int, max: Int): Int {
        if (x > max) return max
        return if (x < min) min else x
    }

    /**
     * Compares two `Size`s based on their areas.
     */
    internal class CompareSizesByArea : Comparator<Size> {
        override fun compare(lhs: Size, rhs: Size): Int {
            // We cast here to ensure the multiplications won't overflow
            return java.lang.Long.signum(
                lhs.width.toLong() * lhs.height -
                        rhs.width.toLong() * rhs.height
            )
        }
    }
}
