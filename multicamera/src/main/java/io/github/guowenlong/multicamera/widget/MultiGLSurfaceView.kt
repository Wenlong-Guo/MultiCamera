package io.github.guowenlong.multicamera.widget

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceHolder
import io.github.guowenlong.multicamera.core.IRenderer

/**
 * Description: MultiCamera 的 GLSurfaceView
 * Author:      郭文龙
 * Date:        2022/4/26 16:00
 * Gmail:       guowenlong20000@sina.com
 */
open class MultiGLSurfaceView(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private lateinit var renderer: IRenderer

    private var scaleGestureDetector: ScaleGestureDetector? = null

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        renderer.surfaceDestroyed(holder)
    }

    fun setMultiRenderer(renderer: IRenderer, renderMode: Int = RENDERMODE_WHEN_DIRTY) {
        /*设置版本*/
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        /*设置Renderer*/
        this.renderer = renderer
        this.setRenderer(renderer)
        /*主动调用渲染*/
        this.renderMode = renderMode
    }

    fun getRenderer(): IRenderer {
        return renderer
    }

    /**
     * 设置手势
     */
    fun setScaleGestureDetector(scaleGestureDetector: ScaleGestureDetector? = null) {
        if (scaleGestureDetector == null) {
            MultiOnScaleGestureListener(renderer.getCamera()).let { listener ->
                ScaleGestureDetector(context, listener).let {
                    this.scaleGestureDetector = it
                    listener.detector = it
                }
            }
        } else {
            this.scaleGestureDetector = scaleGestureDetector
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return scaleGestureDetector?.onTouchEvent(event) ?: super.onTouchEvent(event)
    }
}