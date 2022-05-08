package io.github.guowenlong.camera.core

import android.graphics.SurfaceTexture
import io.github.guowenlong.camera.bean.CameraLensFacing
import io.github.guowenlong.camera.bean.MultiSize

/**
 * Description: 相机接口
 * Author:      郭文龙
 * Date:        2022/5/6 21:19
 * Gmail:       guowenlong20000@sina.com
 */
interface ICamera {

    /**
     * 绑定SurfaceTexture 到 相机预览
     */
    fun bindSurfaceView(surfaceTexture: SurfaceTexture)

    /**
     * 打开相机
     */
    fun openCamera(cameraLensFacing: CameraLensFacing? = null, size: MultiSize? = null)

    /**
     * 相机开始预览
     */
    fun startPreview()

    /**
     * 相机停止预览
     */
    fun stopPreview()

    /**
     * 释放相机资源
     */
    fun releaseCamera()

    /**
     * 切换相机到别的 SurfaceTexture
     */
    fun switchSurfaceView(
        surfaceTexture: SurfaceTexture,
        cameraLensFacing: CameraLensFacing? = null
    ) {
        releaseCamera()
        bindSurfaceView(surfaceTexture)
        openCamera(cameraLensFacing)
    }

    /**
     * 切换相机镜头朝向
     */
    fun switchCamera(cameraLensFacing: CameraLensFacing)


    /**
     * 获取最大缩放比
     */
    fun getMaxZoom(): Int

    /**
     * 设置缩放比
     */
    fun setZoom(zoom: Int)

    /**
     * 拍照
     */
//    fun takePicture()
    /**
     * 开始录像
     */
//    fun startRecord()
    /**9
     * 停止录像
     */
//    fun stopRecord()
    /**
     * 暂停录像 (分段录制)
     */
//    fun pauseRecord()
}