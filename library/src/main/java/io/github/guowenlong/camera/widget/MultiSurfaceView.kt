package io.github.guowenlong.camera.widget

import android.content.Context
import android.graphics.SurfaceTexture
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.hardware.Camera
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import io.github.guowenlong.camera.adapter.CameraPresenter
import io.github.guowenlong.camera.core.ICameraPresenter
import io.github.guowenlong.camera.filter.BaseFilter
import io.github.guowenlong.camera.filter.FilterFactory
import io.github.guowenlong.camera.utils.MatrixUtils
import io.github.guowenlong.camera.utils.SingleThreadUtils
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Description: 多功能 的 GLSurfaceView
 * Author:      郭文龙
 * Date:        2022/4/21 18:23
 * Gmail:       guowenlong20000@sina.com
 */
class MultiSurfaceView(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs), GLSurfaceView.Renderer, OnFrameAvailableListener {

    companion object {
        const val TAG = "MultiSurfaceView"
    }

    private var surfaceTexture: SurfaceTexture? = null
    private var textureId = 0
    private var cameraPresenter: ICameraPresenter
    private val StMatrix = FloatArray(16)
    private var currentFilter: BaseFilter? = null
    private var defaultCameraId: Int = Camera.CameraInfo.CAMERA_FACING_FRONT

    init {
        visibility = View.VISIBLE
        /*设置版本*/
        setEGLContextClientVersion(2)
        /*设置Renderer*/
        setRenderer(this)
        /*主动调用渲染*/
        renderMode = RENDERMODE_WHEN_DIRTY
        /*保存Context当pause时*/
        preserveEGLContextOnPause = true
        cameraDistance = 100f
        cameraPresenter = CameraPresenter()
        cameraPresenter.bindSurfaceView(this)

        currentFilter = FilterFactory.createFilter(
            this.context,
            FilterFactory.FilterType.Beauty
        )
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.i("MultiSurfaceView", "onSurfaceCreated")
    }

    private var viewWidth = 0
    private var viewHeight = 0
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //        mProcessFilter=new CameraDrawProcessFilter(resources);
//        mBeFilter = new GroupFilter(resources);
//        mAfFilter = new GroupFilter(resources);
//        mBeautyFilter = new MagicBeautyFilter();
//        mBeautyFilter = new MagicAntiqueFilter();
//        mSlideFilterGroup = new SlideGpuFilterGroup();

        viewWidth = width
        viewHeight = height
        cameraPresenter.openCamera(defaultCameraId)
        GLES20.glViewport(0, 0, width, height)
        currentFilter?.createProgram()
        textureId = BaseFilter.bindTexture()
        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture?.setOnFrameAvailableListener(this)
        cameraPresenter.startPreview(surfaceTexture!!)
    }

    override fun onDrawFrame(gl: GL10?) {
        surfaceTexture?.updateTexImage()
        surfaceTexture?.getTransformMatrix(StMatrix)
//        MatrixUtils.getShowMatrix(StMatrix,720,1280,1080,2333)
        Log.e("guowenlong", "StMatrix" + StMatrix.contentToString())
//        computeTextureMatrix(720,1280,1080,2333,StMatrix)
        Log.e("guowenlong", "StMatrix" + StMatrix.contentToString())

        currentFilter?.draw(textureId, StMatrix)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        //todo 销毁相机
    }

    fun switchCamera(cameraId: Int? = null) {
        SingleThreadUtils.execute { cameraPresenter.switchCamera(cameraId, surfaceTexture!!) }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        requestRender()
    }

    private fun computeTextureMatrix(cameraWidth :Int,cameraHeight:Int,screenWidth:Int,screenHeight : Int,SM :FloatArray) {
        val cameraRatio = cameraWidth / cameraHeight.toFloat()
        val screenRatio = screenWidth / screenHeight.toFloat()
        Matrix.setIdentityM(SM, 0)
        if (cameraRatio > screenRatio) {
            Matrix.scaleM(SM, 0, 1F, 1 - ((cameraRatio - screenRatio) / 2), 1F)
        } else if (cameraRatio < screenRatio) {
            Matrix.scaleM(SM, 0, 1 - ((screenRatio - cameraRatio) / 2), 1F, 1F)
        }
    }

}