package io.github.guowenlong.multicamera.widget

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import io.github.guowenlong.multicamera.MatrixUtils
import io.github.guowenlong.multicamera.bean.MultiSize
import io.github.guowenlong.multicamera.camera.CameraPresenter
import io.github.guowenlong.multicamera.filter.CameraFilter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Description: MultiCamera 的 渲染器
 * Author:      郭文龙
 * Date:        2022/4/26 15:59
 * Gmail:       guowenlong20000@sina.com
 */
class MultiRenderer(private val surfaceView: MultiGLSurfaceView) : GLSurfaceView.Renderer,
    SurfaceTexture.OnFrameAvailableListener, Preview.OnPreviewOutputUpdateListener {

    private var surfaceTexture: SurfaceTexture? = null

    private var textureId: Int = 0

    private val mtx = FloatArray(16)

    private var filter: CameraFilter? = null

    private var cameraPresenter = CameraPresenter()

    private val cameraSize = MultiSize()

    private val viewSize = MultiSize()

    init {
        cameraPresenter.init(surfaceView.context as LifecycleOwner, this)
    }

    fun onSurfaceDestroy() {
        filter?.release()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        surfaceTexture?.attachToGLContext(textureId)
        surfaceTexture?.setOnFrameAvailableListener(this)
        filter = CameraFilter(surfaceView.context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cameraSize.cover(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        surfaceTexture?.updateTexImage()
        MatrixUtils.getMatrix(
            mtx,
            viewSize.width,
            viewSize.height,
            cameraSize.width,
            cameraSize.height
        )
        filter?.onDraw(mtx, textureId)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        surfaceView.requestRender()
    }

    override fun onUpdated(output: Preview.PreviewOutput?) {
        surfaceTexture = output?.surfaceTexture ?: return
        viewSize.cover(output.textureSize.width, output.textureSize.height)
        Log.d("guowenlong","viewSize.width : ${viewSize.width}")
        Log.d("guowenlong","viewSize.height : ${viewSize.height}")
    }

}