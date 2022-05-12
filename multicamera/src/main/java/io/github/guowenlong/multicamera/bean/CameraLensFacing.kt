package io.github.guowenlong.multicamera.bean

import android.hardware.Camera
import android.hardware.camera2.CameraCharacteristics

/**
 * Description: 相机镜头朝向
 * Author:      郭文龙
 * Date:        2022/5/12 19:08
 * Gmail:       guowenlong20000@sina.com
 */
enum class CameraLensFacing(val camera1: Int, val camera2: Int) {
    FRONT(Camera.CameraInfo.CAMERA_FACING_FRONT, CameraCharacteristics.LENS_FACING_FRONT),
    BACK(Camera.CameraInfo.CAMERA_FACING_BACK,CameraCharacteristics.LENS_FACING_BACK),
}