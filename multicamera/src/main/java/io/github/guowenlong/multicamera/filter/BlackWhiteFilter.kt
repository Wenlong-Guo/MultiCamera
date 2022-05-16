package io.github.guowenlong.multicamera.filter

import android.content.Context

/**
 * Description: 黑白滤镜
 * Author:      郭文龙
 * Date:        2022/5/16 22:00
 * Gmail:       guowenlong20000@sina.com
 */
class BlackWhiteFilter(private val context: Context) : BaseFilter() {

    override fun init() {
        clear()
        createProgram(context, "camera_vs.glsl", "black_white_fs.glsl")
        glPosition = glGetAttribLocation("vPosition")
        glCoord = glGetAttribLocation("vCoord")
        glTexture = glGetUniformLocation("vTexture")
        glMatrix = glGetUniformLocation("vMatrix")
        initBuffer()
    }

    override fun onDrawArraysPre() {

    }

    override fun onDrawArraysAfter() {

    }
}