package io.github.guowenlong.camera.mylibrary

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/4/24 16:49
 * Gmail:       guowenlong20000@sina.com
 */
class MyRenderer(private val surfaceView: MySurfaceView) : GLSurfaceView.Renderer,
    SurfaceTexture.OnFrameAvailableListener, Preview.OnPreviewOutputUpdateListener {

    private var surfaceTexture: SurfaceTexture? = null

    private var textureId: Int = 0

    private val mtx = FloatArray(16)

    private var filter: CameraFilter? = null

    init {
        val lifecycleOwner = surfaceView.context as LifecycleOwner
        CameraHelper.init(lifecycleOwner, this)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        surfaceTexture?.attachToGLContext(textureId)
        surfaceTexture?.setOnFrameAvailableListener(this)

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        filter = CameraFilter(surfaceView.context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        Log.e("guowenlong", "width$width")
        Log.e("guowenlong", "height$height")
    }

    override fun onDrawFrame(p0: GL10?) {
        surfaceTexture?.updateTexImage()
        surfaceTexture?.getTransformMatrix(mtx)
        MatrixUtils.getShowMatrix(mtx, 1080, 1440, 1080, 2501)
        filter?.onDraw(mtx, textureId)
    }

    override fun onFrameAvailable(p0: SurfaceTexture?) {
        surfaceView.requestRender()
    }

    fun onSurfaceDestroy() {
        filter?.release()
    }

    override fun onUpdated(output: Preview.PreviewOutput?) {
        surfaceTexture = output?.surfaceTexture ?: return
        Log.e("guowenlong", "width${output.textureSize.width}")
        Log.e("guowenlong", "height${output.textureSize.height}")
    }
}