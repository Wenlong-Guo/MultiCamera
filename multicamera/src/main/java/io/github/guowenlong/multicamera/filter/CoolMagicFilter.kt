package io.github.guowenlong.multicamera.filter

import android.content.Context
import android.opengl.GLES20
import io.github.guowenlong.multicamera.utils.FilterUtils
import java.nio.ByteBuffer

/**
 * Description: 寒冷滤镜
 * Author:      郭文龙
 * Date:        2022/4/28 22:50
 * Gmail:       guowenlong20000@sina.com
 */
class CoolMagicFilter(private val context: Context) : BaseFilter() {
    private val mToneCurveTexture = intArrayOf(-1)
    private var mToneCurveTextureUniformLocation = 0

    override fun init() {
        clear()
        createProgram(context, "camera_vs.glsl", "cool_fs.glsl")
        glPosition = glGetAttribLocation("vPosition")
        glCoord = glGetAttribLocation("inputTextureCoordinate")
        glTexture = glGetUniformLocation("vTexture")
        glMatrix = glGetUniformLocation("vMatrix")
        initBuffer()
        mToneCurveTextureUniformLocation = GLES20.glGetUniformLocation(glProgram, "curve")
        GLES20.glGenTextures(1, mToneCurveTexture, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mToneCurveTexture[0])
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
        )

        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            256,
            2,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            ByteBuffer.wrap(FilterUtils.getByteArray())
        )

    }

    override fun onDrawArraysPre() {
        if (mToneCurveTexture[0] != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mToneCurveTexture[0])
            GLES20.glUniform1i(mToneCurveTextureUniformLocation, 3)
        }
    }

    override fun onDrawArraysAfter() {
        if (this.mToneCurveTexture[0] != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        }
    }

    override fun release() {
        super.release()
        GLES20.glDeleteTextures(1, mToneCurveTexture, 0)
        mToneCurveTexture[0] = -1
    }
}