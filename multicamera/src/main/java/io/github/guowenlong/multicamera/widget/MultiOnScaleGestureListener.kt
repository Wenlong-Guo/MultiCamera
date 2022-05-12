package io.github.guowenlong.multicamera.widget

import android.view.ScaleGestureDetector
import io.github.guowenlong.multicamera.camera1.Camera1Presenter
import io.github.guowenlong.multicamera.core.ICamera
import kotlin.math.roundToInt

/**
 * Description: MultiCamera 的手势监听器
 * Author:      郭文龙
 * Date:        2022/4/28 0:51
 * Gmail:       guowenlong20000@sina.com
 */
class MultiOnScaleGestureListener(
    private val cameraPresenter: ICamera
) :
    ScaleGestureDetector.OnScaleGestureListener {

    private var mLastScaleFactor = 0f
    private var mScaleFactor = 0f
    lateinit var detector: ScaleGestureDetector

    override fun onScale(p0: ScaleGestureDetector?): Boolean {
        val factorOffset: Float = detector.scaleFactor - mLastScaleFactor
        mScaleFactor += factorOffset
        mLastScaleFactor = detector.scaleFactor
        if (mScaleFactor < 0) {
            mScaleFactor = 0f
        }
        if (mScaleFactor > 1) {
            mScaleFactor = 1f
        }
        cameraPresenter.getMaxZoom().let { maxZoom ->
            val zoomValue = (mScaleFactor * maxZoom).roundToInt() / 2
            cameraPresenter.setZoom(zoomValue)
        }

        return false
    }

    override fun onScaleBegin(p0: ScaleGestureDetector?): Boolean {
        mLastScaleFactor = detector.scaleFactor
        return true
    }

    override fun onScaleEnd(p0: ScaleGestureDetector?) {

    }
}