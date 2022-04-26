package io.github.guowenlong.multicamera.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder

/**
 * Description: MultiCamera 的 GLSurfaceView
 * Author:      郭文龙
 * Date:        2022/4/26 16:00
 * Gmail:       guowenlong20000@sina.com
 */
class MultiGLSurfaceView(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private val renderer: MultiRenderer

    init {
        /*设置版本*/
        setEGLContextClientVersion(2)
        /*设置Renderer*/
        renderer = MultiRenderer(this)
        setRenderer(renderer)
        /*主动调用渲染*/
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        renderer.onSurfaceDestroy()
    }
}