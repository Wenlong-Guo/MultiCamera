package io.github.guowenlong.camera.adapter

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.view.SurfaceView
import io.github.guowenlong.camera.bean.CameraConfig
import io.github.guowenlong.camera.core.ICameraPresenter

/**
 * Description: CameraX的代理类
 * Author:      郭文龙
 * Date:        2022/4/22 19:53
 * Gmail:       guowenlong20000@sina.com
 */
class CameraXPresenter : ICameraPresenter {

    private lateinit var surfaceView: SurfaceView

    override fun bindSurfaceView(surfaceView: SurfaceView) {
        this.surfaceView = surfaceView
    }

    override fun setConfig(config: CameraConfig) {

    }

    override fun getPreWidth(): Int {
        return 0
    }

    override fun getPreHeight(): Int {
        return 0
    }

    override fun openCamera(cameraId: Int) {

    }

    override fun releaseCamera() {

    }

    override fun switchCamera(cameraId: Int?, surfaceTextTure: SurfaceTexture) {

    }


    override fun startPreview(surfaceTextTure: SurfaceTexture) {

    }

    override fun stopPreview() {

    }

    override fun takePicture(
        shutter: Camera.ShutterCallback?,
        raw: Camera.PictureCallback?,
        jpeg: Camera.PictureCallback
    ) {

    }
}