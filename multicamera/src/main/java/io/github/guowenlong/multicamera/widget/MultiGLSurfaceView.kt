package io.github.guowenlong.multicamera.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceHolder
import android.view.View
import io.github.guowenlong.multicamera.camera.CameraPresenter
import io.github.guowenlong.multicamera.camera.TakePictureListener
import io.github.guowenlong.multicamera.filter.BaseFilter
import io.github.guowenlong.multicamera.temp.Camera2Proxy
import io.github.guowenlong.multicamera.utils.SingleThreadUtils

/**
 * Description: MultiCamera 的 GLSurfaceView
 * Author:      郭文龙
 * Date:        2022/4/26 16:00
 * Gmail:       guowenlong20000@sina.com
 */
class MultiGLSurfaceView(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private val renderer: MultiRenderer

//    private val scaleGestureDetector: ScaleGestureDetector

    init {
        /*设置版本*/
        setEGLContextClientVersion(2)
        /*设置Renderer*/
        renderer = MultiRenderer(this)
        setRenderer(renderer)
        /*主动调用渲染*/
        renderMode = RENDERMODE_WHEN_DIRTY
//        MultiOnScaleGestureListener(getCameraPresenter()).let {
//            scaleGestureDetector = ScaleGestureDetector(context, it)
//            it.detector = scaleGestureDetector
//        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        renderer.onSurfaceDestroy()
    }

    fun getCameraPresenter(): Camera2Proxy {
        return renderer.getCameraPresenter()
    }

    fun switchCamera(cameraId: Int? = null) {
        SingleThreadUtils.execute {
            renderer.switchCamera(cameraId)
        }
    }

    fun forceResume() {
        renderer.forceResume()
//        SingleThreadUtils.execute {  }
    }

    fun forcePause(){
        renderer.forcePause()
//        SingleThreadUtils.execute {  }
    }

    fun showMagicFilter(magicFilter: BaseFilter){
        renderer.showMagicFilter(magicFilter)
    }

    fun takePicture(listener: TakePictureListener) {
        renderer.takePicture(listener)
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return scaleGestureDetector.onTouchEvent(event)
//    }
}