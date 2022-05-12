package io.github.guowenlong.multicamera.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceHolder
import io.github.guowenlong.multicamera.bean.CameraLensFacing
import io.github.guowenlong.multicamera.camera1.Camera1Presenter
import io.github.guowenlong.multicamera.camera1.Camera1Renderer
import io.github.guowenlong.multicamera.camera1.TakePictureListener
import io.github.guowenlong.multicamera.core.ICamera
import io.github.guowenlong.multicamera.core.IRenderer
import io.github.guowenlong.multicamera.filter.BaseFilter
import io.github.guowenlong.multicamera.utils.SingleThreadUtils

/**
 * Description: MultiCamera 的 GLSurfaceView
 * Author:      郭文龙
 * Date:        2022/4/26 16:00
 * Gmail:       guowenlong20000@sina.com
 */
open class MultiGLSurfaceView(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private lateinit var renderer: IRenderer

    private lateinit var scaleGestureDetector: ScaleGestureDetector

    init {


    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        renderer.surfaceDestroyed(holder)
    }

    fun setIRenderer(renderer: IRenderer, renderMode: Int = RENDERMODE_WHEN_DIRTY) {
        /*设置版本*/
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        /*设置Renderer*/
        this.renderer = renderer
        this.setRenderer(renderer)
        /*主动调用渲染*/
        this.renderMode = renderMode
        MultiOnScaleGestureListener(getCameraPresenter()).let {
            scaleGestureDetector = ScaleGestureDetector(context, it)
            it.detector = scaleGestureDetector
        }
    }

    fun getRenderer(): IRenderer {
        return renderer
    }

    fun getCameraPresenter(): ICamera {
        return renderer.getCamera()
    }

    fun showMagicFilter(magicFilter: BaseFilter) {
        renderer.showMagicFilter(magicFilter)
    }

    fun takePicture(listener: TakePictureListener) {
        renderer.takePicture(listener)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return scaleGestureDetector.onTouchEvent(event)
    }
}