package io.github.guowenlong.multicamera.camera1

import android.app.Activity
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.SurfaceHolder
import io.github.guowenlong.multicamera.bean.CameraLensFacing
import io.github.guowenlong.multicamera.bean.MultiSize
import io.github.guowenlong.multicamera.core.ICamera
import io.github.guowenlong.multicamera.core.IRenderer
import io.github.guowenlong.multicamera.filter.BaseFilter
import io.github.guowenlong.multicamera.filter.OriginFilter
import io.github.guowenlong.multicamera.utils.*
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Description: MultiCamera 的 渲染器
 * Author:      郭文龙
 * Date:        2022/4/26 15:59
 * Gmail:       guowenlong20000@sina.com
 */
class Camera1Renderer(private val surfaceView: MultiGLSurfaceView) : GLSurfaceView.Renderer,
    IRenderer,
    SurfaceTexture.OnFrameAvailableListener {

    companion object {
        private const val TAG = "MultiRenderer"
    }

    private var surfaceTexture: SurfaceTexture? = null

    private var textureId: Int = 0

    private val mtx = FloatArray(16)

    private var filter: BaseFilter? = null

    private var cameraPresenter: ICamera = Camera1Presenter(surfaceView)

    private val size = MultiSize()

    private var cameraLensFacing = CameraLensFacing.BACK

    private var isEnableDraw = true

    private var takePictureListener: TakeGLPictureListener? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.e(TAG + surfaceTexture, "onSurfaceCreated")

        textureId = OpenGLUtils.createTextureID()
        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture?.setOnFrameAvailableListener(this)
        SingleThreadUtils.execute {
            cameraPresenter.releaseCamera()
            cameraPresenter.openCamera(cameraLensFacing)
            cameraPresenter.startPreview(surfaceTexture)
        }
        filter = OriginFilter(surfaceView.context)
        filter?.init()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//        Log.e(TAG + surfaceTexture, "onSurfaceChanged")
        size.coverViewSize(width, height)
        filter?.turnCameraId(cameraLensFacing)
        size.coverPreviewSize(
            cameraPresenter.getMultiSize().previewWidth,
            cameraPresenter.getMultiSize().previewHeight
        )
        GLES20.glViewport(0, 0, width, height)
    }


    override fun onDrawFrame(gl: GL10?) {
        runAll(runOnDraw)
//        Log.e(TAG, "size:$size")
        MatrixUtils.getMatrix(
            mtx,
            size.previewWidth,
            size.previewHeight,
            size.viewWidth,
            size.viewHeight
        )
        surfaceTexture?.updateTexImage()

        /**
         * 过滤掉颠倒的那帧
         */
        if (!isEnableDraw) {
            surfaceView.postDelayed({ isEnableDraw = true }, 100)
        } else {
            filter?.onDraw(mtx, textureId, cameraLensFacing)
        }

        checkTakePicture(gl)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        surfaceView.requestRender()
    }

    override fun switchCamera(cameraLensFacing: CameraLensFacing?) {
        surfaceTexture?.let {
            SingleThreadUtils.execute {
                this.cameraLensFacing =
                    cameraLensFacing ?: if (this.cameraLensFacing == CameraLensFacing.FRONT) {
                        CameraLensFacing.BACK
                    } else {
                        CameraLensFacing.FRONT
                    }
                isEnableDraw = false
                cameraPresenter.switchCamera(this.cameraLensFacing)
                size.coverPreviewSize(
                    cameraPresenter.getMultiSize().previewWidth,
                    cameraPresenter.getMultiSize().previewHeight
                )
                cameraPresenter.startPreview(surfaceTexture)
                filter?.turnCameraId(this.cameraLensFacing)
            }
        }
    }

    override fun getCamera(): ICamera = cameraPresenter

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.e(TAG + surfaceTexture, "onSurfaceDestroy")
        cameraPresenter.releaseCamera()
    }

    /**
     * 强制恢复
     */
    override fun forceResume() {
        SingleThreadUtils.execute {
            Log.e(TAG + surfaceTexture, "forceResume")
            surfaceView.onResume()
            cameraPresenter.switchCamera(cameraLensFacing)
            cameraPresenter.startPreview(surfaceTexture)
        }
    }

    override fun forcePause() {
        SingleThreadUtils.execute {
            surfaceView.onPause()
            Log.e(TAG + surfaceTexture, "forcePause")
            cameraPresenter.stopPreview()
            cameraPresenter.releaseCamera()
        }
    }

    /**
     * 无声拍照
     */
    override fun takePicture(listener: TakeGLPictureListener) {
        takePictureListener = listener
    }

    override fun takePicture(
        shutterCallback: Camera.ShutterCallback?,
        raw: Camera.PictureCallback?,
        listener: TakeCameraPictureListener
    ) {
        getCamera().takePicture(shutterCallback, raw) { bytes, _ ->
            bytes?.let {
                var bitmap = BitmapUtils.bytesToBitmap(bytes)
                bitmap = if (cameraLensFacing == CameraLensFacing.FRONT) {
                    CameraUtils.flip(
                        CameraUtils.rotateBitmap(bitmap, -90F),
                        isX = true,
                        isY = false
                    )
                } else {
                    CameraUtils.rotateBitmap(bitmap, 90F)
                }
                listener.onCollect(bitmap, bytes, getCamera())
            }
        }
    }

    /**
     * 监控是否需要拍照
     */
    private fun checkTakePicture(gl: GL10?) {
        if (takePictureListener == null) return
        GLSurfaceViewUtils.createBitmapFromGLSurface(0, 0, size.viewWidth, size.viewHeight, gl)
            ?.let {
                (surfaceView.context as Activity).runOnUiThread {
                    takePictureListener?.onCollect(it)
                    takePictureListener = null
                }
            }
    }

    override fun showMagicFilter(filter: BaseFilter) {
        runOnDraw {
            this.filter?.release()
            this.filter = filter
            this.filter?.init()
        }
    }

    private val runOnDraw: Queue<Runnable> = LinkedList()

    private fun runOnDraw(runnable: Runnable?) {
        synchronized(runOnDraw) {
            runOnDraw.add(runnable)
        }
    }

    private fun runAll(queue: Queue<Runnable>) {
        synchronized(queue) {
            while (!queue.isEmpty()) {
                queue.poll()?.run()
            }
        }
    }
}