package io.github.guowenlong.multicamera.core

import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.view.SurfaceHolder
import io.github.guowenlong.multicamera.bean.CameraLensFacing
import io.github.guowenlong.multicamera.camera1.TakeCameraPictureListener
import io.github.guowenlong.multicamera.camera1.TakeGLPictureListener
import io.github.guowenlong.multicamera.filter.BaseFilter

/**
 * Description: 渲染器的接口
 * Author:      郭文龙
 * Date:        2022/5/12 19:07
 * Gmail:       guowenlong20000@sina.com
 */
interface IRenderer : GLSurfaceView.Renderer {

    fun getCamera(): ICamera

    fun surfaceDestroyed(holder: SurfaceHolder)

    fun forceResume()

    fun forcePause()

    fun showMagicFilter(filter: BaseFilter)

    fun switchCamera(cameraLensFacing: CameraLensFacing? = null)

    fun takePicture(listener: TakeGLPictureListener)

    /**
     * 经过转换角度和裁剪的比例
     */
    fun takePicture(
        shutterCallback: Camera.ShutterCallback? = null,
        raw: Camera.PictureCallback? = null,
        listener: TakeCameraPictureListener
    )
}