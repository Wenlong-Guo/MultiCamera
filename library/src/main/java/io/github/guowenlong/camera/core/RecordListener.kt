package io.github.guowenlong.camera.core

/**
 * Description: 录制的回调接口
 * Author:      郭文龙
 * Date:        2022/4/22 20:42
 * Gmail:       guowenlong20000@sina.com
 */
interface RecordListener {
    /**
     * 录视频开始
     */
    fun onRecordStart()

    /**
     * 录视频中
     * @param milliSecond 毫秒
     */
    fun onRecordProgressing(milliSecond: Long)

    /**
     * 录视频结束
     */
    fun onRecordFinished()
}