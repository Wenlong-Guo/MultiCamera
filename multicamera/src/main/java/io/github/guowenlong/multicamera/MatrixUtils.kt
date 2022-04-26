package io.github.guowenlong.multicamera

import android.opengl.Matrix

/**
 * Description: Matrix工具类
 * Author:      郭文龙
 * Date:        2022/4/26 21:47
 * Gmail:       guowenlong20000@sina.com
 */
object MatrixUtils {

    fun getMatrix(
        matrix: FloatArray,
        cameraWidth: Int,
        cameraHeight: Int,
        viewWidth: Int,
        viewHeight: Int
    ) {
        if (cameraHeight > 0 && cameraWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            val sWhView = viewWidth.toFloat() / viewHeight
            val sWhImg = cameraWidth.toFloat() / cameraHeight
            val projection = FloatArray(16)
            val camera = FloatArray(16)
            if (sWhImg > sWhView) {
                Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1f, 1f, 1f, 3f)
            } else {
                Matrix.orthoM(projection, 0, -1f, 1f, -sWhImg / sWhView, sWhImg / sWhView, 1f, 3f)
            }
            Matrix.setLookAtM(camera, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0)
        }
    }
}