package io.github.guowenlong.multicamera.filter

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import io.github.guowenlong.multicamera.utils.AssetsUtils
import io.github.guowenlong.multicamera.utils.OpenGLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Description: 基础过滤器
 * Author:      郭文龙
 * Date:        2022/4/26 16:06
 * Gmail:       guowenlong20000@sina.com
 */
abstract class BaseFilter {
    companion object {
        const val TAG = "BaseFilter"
    }

    var glProgram: Int? = null
    var glPosition = 0
    var glCoord = 0
    var glTexture = 0
    var glMatrix = 0
    var cameraId: Int = 0
    var vertexBuffer: FloatBuffer? = null
    var backTextureBuffer: FloatBuffer? = null
    var frontTextureBuffer: FloatBuffer? = null

    abstract fun onDraw(mtx: FloatArray, textureId: Int, cameraId: Int)

    /**
     * 删除 program
     */
    open fun release() {
        glProgram?.let { GLES20.glDeleteProgram(it) }
    }

    open fun turnCameraId(cameraId: Int) {
        this.cameraId = cameraId
        initBuffer()
    }

    /**
     * 清除画布
     */
    open fun clear() {
        //黑色
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    open fun createProgram(context: Context, vertexGLSL: String, fragmentGLSL: String) {
        glProgram = OpenGLUtils.createProgram(
            AssetsUtils.readAssets2String(context, vertexGLSL),
            AssetsUtils.readAssets2String(context, fragmentGLSL)
        )
        glProgram?.let {
            GLES20.glUseProgram(it)
        }
    }

    open fun glGetAttribLocation(attribute: String): Int {
        val glAttribute = glProgram?.let {
            GLES20.glGetAttribLocation(it, attribute)
        } ?: run {
            Log.e(TAG, "program is null")
            0
        }
        return glAttribute
    }

    open fun glGetUniformLocation(uni: String): Int {
        val glUniform = glProgram?.let {
            GLES20.glGetUniformLocation(it, uni)
        } ?: run {
            Log.e(TAG, "program is null")
            0
        }
        return glUniform
    }

    open fun bindTexture(textureId: Int) {
//        if (textureId != OpenGLUtils.NO_TEXTURE) {
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
//            GLES20.glUniform1i(glTexture, 0)
//        }
    }

    open fun initBuffer() {
        vertexBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer?.clear()
        vertexBuffer?.put(OpenGLUtils.VERTEX)?.position(0)
        backTextureBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer()
        backTextureBuffer?.clear()
        backTextureBuffer?.put(OpenGLUtils.TEXTURE_BACK)
        frontTextureBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer()
        frontTextureBuffer?.clear()
        frontTextureBuffer?.put(OpenGLUtils.TEXTURE_FRONT)
    }
}