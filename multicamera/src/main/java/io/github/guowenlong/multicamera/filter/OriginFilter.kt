package io.github.guowenlong.multicamera.filter

import android.content.Context

/**
 * Description: 仅展示的过滤器
 * Author:      郭文龙
 * Date:        2022/4/26 16:10
 * Gmail:       guowenlong20000@sina.com
 */
class OriginFilter(private val context: Context) : BaseFilter() {

    override fun init() {
        clear()
        createProgram(context, "camera_vs.glsl", "camera_fs.glsl")
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