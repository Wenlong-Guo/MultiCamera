package io.github.guowenlong.multicamera.bean

import android.hardware.Camera

/**
 * Description: 相机参数
 * Author:      郭文龙
 * Date:        2022/4/22 20:44
 * Gmail:       guowenlong20000@sina.com
 */
data class CameraConfig(
    var cameraId: Int = Camera.CameraInfo.CAMERA_FACING_FRONT,
    var focusMode: String = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
)