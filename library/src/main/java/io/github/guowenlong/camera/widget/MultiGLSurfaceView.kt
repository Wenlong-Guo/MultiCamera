package io.github.guowenlong.camera.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder
import io.github.guowenlong.camera.core.IRenderer

/**
 * Description: 多功能 GLSurfaceView
 *              基于OpenGL 2.0
 * Author:      郭文龙
 * Date:        2022/5/6 21:15
 * Gmail:       guowenlong20000@sina.com
 */
class MultiGLSurfaceView(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private val renderer = MultiRenderer(this)

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        this.renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun getRenderer(): IRenderer {
        return renderer
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        renderer.surfaceDestroyed(holder)
    }
}