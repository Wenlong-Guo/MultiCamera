package io.github.guowenlong.multicamera.bean

/**
 * Description: 多种Size的实体类
 * Author:      郭文龙
 * Date:        2022/4/26 16:15
 * Gmail:       guowenlong20000@sina.com
 */
data class MultiSize(
    var width: Int = 0,
    var height: Int = 0,
) {
    fun cover(width: Int, height: Int) {
        if (height > width) {
            this.width = width
            this.height = height
        } else {
            this.width = height
            this.height = width
        }
    }
}
