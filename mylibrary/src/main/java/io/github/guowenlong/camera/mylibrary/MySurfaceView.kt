package io.github.guowenlong.camera.mylibrary

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder

/**
 * Description: 多功能 的 GLSurfaceView
 * Author:      郭文龙
 * Date:        2022/4/21 18:23
 * Gmail:       guowenlong20000@sina.com
 */
class MySurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    companion object {
        const val TAG = "MultiSurfaceView"
    }

    private val renderer: MyRenderer

    init {
        /*设置版本*/
        setEGLContextClientVersion(2)
        /*设置Renderer*/
        renderer = MyRenderer(this)
        setRenderer(renderer)
        /*主动调用渲染*/
        renderMode = RENDERMODE_WHEN_DIRTY

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)
        renderer.onSurfaceDestroy()
    }
}