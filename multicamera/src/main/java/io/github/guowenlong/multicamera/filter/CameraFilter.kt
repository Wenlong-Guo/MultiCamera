package io.github.guowenlong.multicamera.filter

import android.content.Context
import android.opengl.GLES20

/**
 * Description: 仅展示的过滤器
 * Author:      郭文龙
 * Date:        2022/4/26 16:10
 * Gmail:       guowenlong20000@sina.com
 */
class CameraFilter(context: Context) : BaseFilter() {

    init {
        clear()
        createProgram(context, "camera_vs.glsl", "camera_fs.glsl")
        glPosition = glGetAttribLocation("vPosition")
        glCoord = glGetAttribLocation("vCoord")
        glTexture = glGetUniformLocation("vTexture")
        glMatrix = glGetUniformLocation("vMatrix")
        initBuffer()
    }

    override fun onDraw(mtx: FloatArray, textureId: Int) {
        vertexBuffer?.position(0)
        GLES20.glVertexAttribPointer(glPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(glPosition)

        textureBuffer?.position(0)
        GLES20.glVertexAttribPointer(glCoord, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glEnableVertexAttribArray(glCoord)

        bindTexture(textureId)

        GLES20.glUniformMatrix4fv(glMatrix, 1, false, mtx, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }
}