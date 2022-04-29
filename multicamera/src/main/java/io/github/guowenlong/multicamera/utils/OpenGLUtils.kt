package io.github.guowenlong.multicamera.utils

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import javax.microedition.khronos.opengles.GL10

/**
 * Description: GLES20的工具类
 * Author:      郭文龙
 * Date:        2022/4/26 19:12
 * Gmail:       guowenlong20000@sina.com
 */
object OpenGLUtils {

    private const val TAG = "OpenGLUtils"
    const val NO_TEXTURE = -1

    /**
     * 顶点坐标
     */
    val VERTEX = floatArrayOf(
        -1.0f, -1.0f,
        1f, -1f,
        -1f, 1f,
        1f, 1f
    )

    /**
     * 纹理坐标 后置摄像头
     */
    val TEXTURE_BACK = floatArrayOf(
        1f, 1f,
        1f, 0f,
        0f, 1f,
        0f, 0f,
    )

    /**
     * 纹理坐标 前置摄像头
     */
    val TEXTURE_FRONT = floatArrayOf(
        0f, 1f,
        0f, 0f,
        1f, 1f,
        1f, 0f,
    )

    fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertex: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertex == 0) return 0
        val fragment: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragment == 0) return 0
        var program = GLES20.glCreateProgram()
        if (program != 0) {
            GLES20.glAttachShader(program, vertex)
            GLES20.glAttachShader(program, fragment)
            GLES20.glLinkProgram(program)
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "createProgram error: ${GLES20.glGetProgramInfoLog(program)}")
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    private fun loadShader(shaderType: Int, source: String): Int {
        var shaderHandle: Int = GLES20.glCreateShader(shaderType)
        if (shaderHandle != 0) {
            //加载shader源码
            GLES20.glShaderSource(shaderHandle, source)
            //编译shader
            GLES20.glCompileShader(shaderHandle)
            val compileStatus = IntArray(1)
            //检查shader状态
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                //输入shader异常日志
                Log.e("GLTools", "Error compile shader:${GLES20.glGetShaderInfoLog(shaderHandle)}")
                //删除shader
                GLES20.glDeleteShader(shaderHandle)
                shaderHandle = 0
            }
        }
        return shaderHandle
    }

    fun createTextureID(): Int {
        val texture = IntArray(1)
        GLES20.glGenTextures(1, texture, 0) //第一个参数表示创建几个纹理对象，并将创建好的纹理对象放置到第二个参数中去
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0])
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE
        )
        return texture[0]
    }

    fun loadTexture(context: Context, name: String): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)
        if (textureHandle[0] != 0) {

            // Read in the resource
            val bitmap: Bitmap? = AssetsUtils.getImageFromAssetsFile(context, name)

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            // Set filtering
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE
            )
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap?.recycle()
        }
        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }
        return textureHandle[0]
    }
}