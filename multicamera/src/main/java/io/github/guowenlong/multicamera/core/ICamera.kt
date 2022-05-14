package io.github.guowenlong.multicamera.core

import android.graphics.SurfaceTexture
import android.hardware.Camera
import io.github.guowenlong.multicamera.bean.CameraLensFacing
import io.github.guowenlong.multicamera.bean.MultiSize
import io.github.guowenlong.multicamera.camera1.TakeCameraPictureListener

/**
 * Description: 相机接口
 * Author:      郭文龙
 * Date:        2022/5/12 19:06
 * Gmail:       guowenlong20000@sina.com
 */
interface ICamera {

    /**
     * 打开相机
     */
    fun openCamera(cameraLensFacing: CameraLensFacing, size: MultiSize? = null)

    /**
     * 相机开始预览
     */
    fun startPreview(surfaceTexture: SurfaceTexture? = null)

    /**
     * 相机停止预览
     */
    fun stopPreview()

    /**
     * 释放相机资源
     */
    fun releaseCamera()

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
     * 获取size
     */
    fun getMultiSize(): MultiSize

    /**
     * 原生拍照
     * 注:需要自己转换角度和裁剪比例
     */
    fun takePicture(
        shutterCallback: Camera.ShutterCallback? = null,
        raw: Camera.PictureCallback? = null,
        jpeg: Camera.PictureCallback
    )

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