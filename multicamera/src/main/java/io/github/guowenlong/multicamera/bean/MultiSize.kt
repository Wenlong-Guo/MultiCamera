package io.github.guowenlong.multicamera.bean

/**
 * Description: 多种Size的实体类
 * Author:      郭文龙
 * Date:        2022/4/26 16:15
 * Gmail:       guowenlong20000@sina.com
 */
data class MultiSize(
    /**
     * 相机预览的宽度
     */
    var previewWidth: Int = 0,
    /**
     * 相机预览的高度
     */
    var previewHeight: Int = 0,
    /**
     * 相机拍摄照片的宽度
     */
    var pictureWidth: Int = 0,
    /**
     * 相机拍摄照片的高度
     */
    var pictureHeight: Int = 0,
    /**
     * GLSurfaceView的宽度
     */
    var viewWidth: Int = 0,
    /**
     * GLSurfaceView的高度
     */
    var viewHeight: Int = 0
) {
    /**
     * 覆盖相机预览的宽高
     */
    fun coverPreviewSize(width: Int, height: Int) {
        if (height > width) {
            this.previewWidth = width
            this.previewHeight = height
        } else {
            this.previewWidth = height
            this.previewHeight = width
        }
    }
    /**
     * 覆盖相机拍照片的宽高
     */
    fun coverPictureSize(width: Int, height: Int) {
        if (height > width) {
            this.pictureWidth = width
            this.pictureHeight = height
        } else {
            this.pictureWidth = height
            this.pictureHeight = width
        }
    }

    /**
     * 覆盖GLSurfaceView的宽高
     */
    fun coverViewSize(width: Int, height: Int) {
        if (height > width) {
            this.viewWidth = width
            this.viewHeight = height
        } else {
            this.viewWidth = height
            this.viewHeight = width
        }
    }

    override fun toString(): String {
        return "MultiSize(previewWidth=$previewWidth, previewHeight=$previewHeight, pictureWidth=$pictureWidth, pictureHeight=$pictureHeight, viewWidth=$viewWidth, viewHeight=$viewHeight)"
    }

}
