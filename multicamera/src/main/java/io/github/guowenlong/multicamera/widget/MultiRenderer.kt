package io.github.guowenlong.multicamera.widget

import android.app.Activity
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import io.github.guowenlong.multicamera.bean.CameraConfig
import io.github.guowenlong.multicamera.bean.MultiSize
import io.github.guowenlong.multicamera.camera.CameraPresenter
import io.github.guowenlong.multicamera.camera.TakePictureListener
import io.github.guowenlong.multicamera.filter.BaseFilter
import io.github.guowenlong.multicamera.filter.CoolMagicFilter
import io.github.guowenlong.multicamera.filter.OriginFilter
import io.github.guowenlong.multicamera.utils.GLSurfaceViewUtils
import io.github.guowenlong.multicamera.utils.MatrixUtils
import io.github.guowenlong.multicamera.utils.OpenGLUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Description: MultiCamera 的 渲染器
 * Author:      郭文龙
 * Date:        2022/4/26 15:59
 * Gmail:       guowenlong20000@sina.com
 */
class MultiRenderer(private val surfaceView: MultiGLSurfaceView) : GLSurfaceView.Renderer,
    SurfaceTexture.OnFrameAvailableListener {

    private var surfaceTexture: SurfaceTexture? = null

    private var textureId: Int = 0

    private val mtx = FloatArray(16)

    private var filter: BaseFilter? = null

    private var cameraPresenter = CameraPresenter(surfaceView)

    private val cameraSize = MultiSize()

    private var viewSize = MultiSize()

    private var cameraConfig = CameraConfig()

    private var isEnableDraw = true

    private var takePictureListener: TakePictureListener? = null

    fun onSurfaceDestroy() {
        filter?.release()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        textureId = OpenGLUtils.createTextureID()
        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture?.setOnFrameAvailableListener(this)
        cameraPresenter.releaseCamera()
        cameraPresenter.openCamera(cameraConfig.cameraId)
        cameraPresenter.startPreview(surfaceTexture)
        filter = CoolMagicFilter(surfaceView.context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cameraSize.cover(width, height)
        filter?.turnCameraId(cameraConfig.cameraId)
        viewSize = cameraPresenter.cameraSize
        GLES20.glViewport(0, 0, width, height)
    }


    override fun onDrawFrame(gl: GL10?) {
        MatrixUtils.getMatrix(
            mtx,
            viewSize.width,
            viewSize.height,
            cameraSize.width,
            cameraSize.height
        )

        surfaceTexture?.updateTexImage()

        /**
         * 过滤掉颠倒的那帧
         */
        if (!isEnableDraw) {
            surfaceView.postDelayed({ isEnableDraw = true }, 100)
        } else {
            filter?.onDraw(mtx, textureId, cameraConfig.cameraId)
        }

        checkTakePicture(gl)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        surfaceView.requestRender()
    }

    fun switchCamera(cameraId: Int?) {
        surfaceTexture?.let {
            cameraConfig.cameraId =
                cameraId ?: if (cameraConfig.cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    Camera.CameraInfo.CAMERA_FACING_BACK
                } else {
                    Camera.CameraInfo.CAMERA_FACING_FRONT
                }
            isEnableDraw = false
            cameraPresenter.switchCamera(cameraConfig.cameraId)
            viewSize = cameraPresenter.cameraSize
            cameraPresenter.startPreview(surfaceTexture)
            filter?.turnCameraId(cameraConfig.cameraId)
        }
    }

    fun getCameraPresenter(): CameraPresenter {
        return cameraPresenter
    }

    /**
     * 强制恢复
     */
    fun forceResume() {
        cameraPresenter.switchCamera(cameraConfig.cameraId)
        cameraPresenter.startPreview(surfaceTexture)
    }

    fun forcePause() {
        cameraPresenter.stopPreview()
    }

    /**
     * 无声拍照
     */
    fun takePicture(listener: TakePictureListener) {
        takePictureListener = listener
    }

    /**
     * 监控是否需要拍照
     */
    private fun checkTakePicture(gl: GL10?) {
        if (takePictureListener == null) return
        GLSurfaceViewUtils.createBitmapFromGLSurface(0, 0, cameraSize.width, cameraSize.height, gl)
            ?.let {
                (surfaceView.context as Activity).runOnUiThread {
                    takePictureListener?.onCollect(it)
                    takePictureListener = null
                }
            }
    }

    fun showMagicFilter(magicFilter: BaseFilter) {
        filter = magicFilter
    }
}