package io.github.guowenlong.camera.bean

/**
 * Description: 多种Size的实体类
 * Author:      郭文龙
 * Date:        2022/5/6 22:19
 * Gmail:       guowenlong20000@sina.com
 */
data class MultiSize(
    var previewWidth: Int = 0,
    var previewHeight: Int = 0,
    var pictureWidth: Int = 0,
    var pictureHeight: Int = 0,
    var viewWidth: Int = 0,
    var viewHeight: Int = 0
) {
    fun coverPreviewSize(width: Int, height: Int) {
        if (height > width) {
            this.previewWidth = width
            this.previewHeight = height
        } else {
            this.previewWidth = height
            this.previewHeight = width
        }
    }

    fun coverPictureSize(width: Int, height: Int) {
        if (height > width) {
            this.pictureWidth = width
            this.pictureHeight = height
        } else {
            this.pictureWidth = height
            this.pictureHeight = width
        }
    }

    fun coverViewSize(width: Int, height: Int) {
        if (height > width) {
            this.viewWidth = width
            this.viewHeight = height
        } else {
            this.viewWidth = height
            this.viewHeight = width
        }
    }
}
