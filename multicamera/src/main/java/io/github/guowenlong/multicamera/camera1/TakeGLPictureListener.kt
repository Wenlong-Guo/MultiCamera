package io.github.guowenlong.multicamera.camera1

import android.graphics.Bitmap

/**
 * Description: 图片收集监听器
 * Author:      郭文龙
 * Date:        2022/4/27 16:45
 * Gmail:       guowenlong20000@sina.com
 */
interface TakeGLPictureListener {
    fun onCollect(bitmap: Bitmap)
}