package io.github.guowenlong.camera.mylibrary

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import io.github.guowenlong.camera.mylibrary.OpenGLUtils.TEXTURE
import io.github.guowenlong.camera.mylibrary.OpenGLUtils.VERTEX
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/4/24 17:10
 * Gmail:       guowenlong20000@sina.com
 */
class CameraFilter(context: Context) {
    private var mProgramHandle = 0
    private var vPosition = 0
    private var vCoord = 0
    private var vTexture = 0
    private var vMatrix = 0
    var vertexBuffer: FloatBuffer? = null
    var textureBuffer: FloatBuffer? = null

    init {
        val vertexCode =
            AssetsUtils.getStringFromAssert(
                context,
                "camera_vs.glsl"
            )
        val fragmentCode =
            AssetsUtils.getStringFromAssert(
                context,
                "camera_fs.glsl"
            )
        Log.e("guowenlong",vertexCode)
        Log.e("guowenlong",fragmentCode)
        mProgramHandle = GLTools.createAndLinkProgram(vertexCode, fragmentCode)
        GLES20.glUseProgram(mProgramHandle)

        vPosition = GLES20.glGetAttribLocation(mProgramHandle, "vPosition")
        vCoord = GLES20.glGetAttribLocation(mProgramHandle, "vCoord")
        vTexture = GLES20.glGetUniformLocation(mProgramHandle, "vTexture")
        vMatrix = GLES20.glGetUniformLocation(mProgramHandle, "vMatrix")

        vertexBuffer = ByteBuffer.allocateDirect(VERTEX.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer?.clear()
        vertexBuffer?.put(VERTEX)
        textureBuffer = ByteBuffer.allocateDirect(TEXTURE.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureBuffer?.clear()
        textureBuffer?.put(TEXTURE)
    }

    fun onDraw(mtx: FloatArray, texture: Int) {
        vertexBuffer?.position(0)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(vPosition)

        textureBuffer?.position(0)
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLES20.glUniform1i(vTexture, 0)

        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    fun release() {
        GLES20.glDeleteProgram(mProgramHandle)
    }
}