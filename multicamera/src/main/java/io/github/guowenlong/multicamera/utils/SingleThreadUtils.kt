package io.github.guowenlong.multicamera.utils

import java.util.concurrent.Executors

/**
 * Description: 单线程的线程池工具类
 * Author:      郭文龙
 * Date:        2022/4/21 22:46
 * Gmail:       guowenlong20000@sina.com
 */
object SingleThreadUtils {

    private val pool by lazy { Executors.newSingleThreadExecutor() }

    fun execute(runnable: Runnable) {
        pool.execute(runnable)
    }
}