package io.github.guowenlong.camera.core

import android.opengl.GLSurfaceView
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Description: 渲染器的接口
 * Author:      郭文龙
 * Date:        2022/5/6 21:20
 * Gmail:       guowenlong20000@sina.com
 */
interface IRenderer {

    fun bindCamera(camera: ICamera)

    fun getCamera(): ICamera?

    fun setIsAutoPreview(isAuto: Boolean)

    fun surfaceDestroyed(holder: SurfaceHolder)

    fun forceResume()

    fun forceStop()
}