package io.github.guowenlong.camera.widget

import android.app.Activity
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.SurfaceHolder
import io.github.guowenlong.camera.bean.CameraLensFacing
import io.github.guowenlong.camera.bean.MultiSize
import io.github.guowenlong.camera.camera.Camera2Presenter
import io.github.guowenlong.camera.camera.Camera2Proxy
import io.github.guowenlong.camera.core.ICamera
import io.github.guowenlong.camera.core.IRenderer
import io.github.guowenlong.camera.filter.BaseFilter
import io.github.guowenlong.camera.filter.OriginFilter
import io.github.guowenlong.camera.utils.MatrixUtils
import io.github.guowenlong.camera.utils.OpenGLUtils
import io.github.guowenlong.camera.utils.SingleThreadUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Description: 多功能渲染器
 * Author:      郭文龙
 * Date:        2022/5/6 21:26
 * Gmail:       guowenlong20000@sina.com
 */
class MultiRenderer(private val surfaceView: GLSurfaceView) : GLSurfaceView.Renderer, IRenderer {

    companion object {
        private const val TAG = "MultiRenderer"
    }

    private var camera = Camera2Proxy(surfaceView.context as Activity)

    private var surfaceTexture: SurfaceTexture? = null

    private var textureId: Int? = null

    private val mtx = FloatArray(16)

    private var filter: BaseFilter? = null

    private val size = MultiSize()

    private var lensFacing: CameraLensFacing = CameraLensFacing.FRONT

    private val frameAvailableListener =
        SurfaceTexture.OnFrameAvailableListener { surfaceView.requestRender() }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.e(TAG, "onSurfaceCreated")
        OpenGLUtils.createTextureID().also {
            textureId = it
            surfaceTexture = SurfaceTexture(it)
        }

        surfaceTexture?.also {
            it.setOnFrameAvailableListener(frameAvailableListener)
            camera.openCamera(0)
            camera.setPreviewSurface(it)
        }

        filter = OriginFilter(surfaceView.context)
        filter?.init()
    }

    override fun onSurfaceChanged(gl10: GL10?, width: Int, height: Int) {
        Log.e(TAG, "onSurfaceChanged")
        size.coverPreviewSize(width, height)
        size.coverViewSize(width, height)
        filter?.turnCameraId(lensFacing.camera1)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        MatrixUtils.getMatrix(
            mtx,
            size.previewWidth,
            size.previewHeight,
            size.viewWidth,
            size.viewHeight
        )
        surfaceTexture?.updateTexImage()

//        /**
//         * 过滤掉颠倒的那帧
//         */
//        if (!isEnableDraw) {
//            surfaceView.postDelayed({ isEnableDraw = true }, 100)
//        } else {
//            filter?.onDraw(mtx, textureId, cameraConfig.cameraId)
//        }
        textureId?.let { filter?.onDraw(mtx, it, 0) }

    }

    override fun bindCamera(camera: ICamera) {
//        this.camera = camera
    }

    override fun getCamera(): ICamera? {
//        return camera
        return null
    }

    override fun setIsAutoPreview(isAuto: Boolean) {
        //todo
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.e(TAG, "surfaceDestroyed")
        SingleThreadUtils.execute {
            camera.stopPreview()
            camera.releaseCamera()
        }
    }

    override fun forceResume() {
        if (surfaceTexture == null) return
        Log.e(TAG, "forceResume")
        surfaceTexture?.also {
            it.setOnFrameAvailableListener(frameAvailableListener)
            camera.releaseCamera()
            camera.openCamera(0)
            camera.setPreviewSurface(it)
        }
    }

    override fun forceStop() {

    }
}