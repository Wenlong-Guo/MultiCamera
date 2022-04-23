package io.github.guowenlong.camera.bean

/**
 * Description: Record的参数配置
 * Author:      郭文龙
 * Date:        2022/4/22 20:39
 * Gmail:       guowenlong20000@sina.com
 */
data class RecordConfig(
    var recordWidth: Int,
    var recordHeight: Int,
    var videoRate: Int,
    var frame: Int
)
