package io.github.guowenlong.multicamera.widget

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import io.github.guowenlong.multicamera.bean.CameraConfig
import io.github.guowenlong.multicamera.bean.MultiSize
import io.github.guowenlong.multicamera.camera.CameraPresenter
import io.github.guowenlong.multicamera.filter.CameraFilter
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

    private var filter: CameraFilter? = null

    private var cameraPresenter = CameraPresenter(surfaceView)

    private val cameraSize = MultiSize()

    private var viewSize = MultiSize()

    private var cameraConfig = CameraConfig()

    private var isEnableDraw = true

    fun onSurfaceDestroy() {
        filter?.release()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        textureId = OpenGLUtils.createTextureID()
        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture?.setOnFrameAvailableListener(this)
        filter = CameraFilter(surfaceView.context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cameraSize.cover(width, height)
        cameraPresenter.openCamera(cameraConfig.cameraId)
        filter?.turnCameraId(cameraConfig.cameraId)
        surfaceTexture?.let { cameraPresenter.startPreview(it) }
        viewSize = cameraPresenter.cameraSize
        GLES20.glViewport(0, 0, width, height)
        Log.e("guowenlong", "camerasize:$cameraSize")
        Log.e("guowenlong", "viewSize:$viewSize")
        Log.e("guowenlong camera", "onSurfaceChanged :${cameraConfig.cameraId}")
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
            isEnableDraw = true
        } else {
            filter?.onDraw(mtx, textureId, cameraConfig.cameraId)
        }
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
            surfaceTexture?.let { cameraPresenter.startPreview(it) }
            filter?.turnCameraId(cameraConfig.cameraId)
            Log.e("guowenlong", "camerasize:$cameraSize")
            Log.e("guowenlong", "viewSize:$viewSize")
        }
    }
}