package io.github.guowenlong.multicamera.utils

import android.opengl.GLES20
import android.util.Log

/**
 * Description: GLES20的工具类
 * Author:      郭文龙
 * Date:        2022/4/26 19:12
 * Gmail:       guowenlong20000@sina.com
 */
object OpenGLUtils {

    private const val TAG = "OpenGLUtils"

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
     * 纹理坐标
     */
    val TEXTURE = floatArrayOf(
        1f, 1f,
        1f, 0f,
        0f, 1f,
        0f, 0f,
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
}