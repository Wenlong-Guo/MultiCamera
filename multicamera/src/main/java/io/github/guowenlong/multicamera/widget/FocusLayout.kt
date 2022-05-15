package io.github.guowenlong.multicamera.widget

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import io.github.guowenlong.multicamera.R

/**
 * Description: 聚焦View
 * Author:      郭文龙
 * Date:        2022/5/15 22:44
 * Gmail:       guowenlong20000@sina.com
 */
class FocusLayout(context: Context, attrs: AttributeSet? = null) :
    RelativeLayout(context, attrs) {
    companion object {
        private const val TAG = "FocusLayout"
        private const val NO_ID = -1
    }

    private var focusingId: Int = NO_ID
    private var focusSucceedId: Int = NO_ID
    private var focusFailedId: Int = NO_ID
    private var imageWidth: Int = 80
    private var imageHeight: Int = 80

    private val focusAnim by lazy {
        AnimationUtils.loadAnimation(getContext(), R.anim.focusview_show)
    }

    private val focusHandler by lazy { Handler(Looper.getMainLooper()) }

    private val goneRunnable by lazy { Runnable { image.visibility = GONE } }

    private val view by lazy {
        LayoutInflater.from(context).inflate(R.layout.focus_layout, this, true)
    }

    private val image by lazy { view.findViewById<ImageView>(R.id.iv_content) }

    init {
        image.visibility = View.INVISIBLE
        val a = context.obtainStyledAttributes(attrs, R.styleable.FocusLayout)
        focusingId = a.getResourceId(R.styleable.FocusLayout_focus_focusing_id, NO_ID)
        focusSucceedId = a.getResourceId(R.styleable.FocusLayout_focus_success_id, NO_ID)
        focusFailedId = a.getResourceId(R.styleable.FocusLayout_focus_fail_id, NO_ID)
        imageWidth = a.getDimensionPixelOffset(R.styleable.FocusLayout_focus_width, 80)
        imageHeight = a.getDimensionPixelOffset(R.styleable.FocusLayout_focus_height, 80)
        val layoutParams = image.layoutParams as LayoutParams
        layoutParams.width = imageWidth
        layoutParams.height = imageHeight
        image.layoutParams = layoutParams
        a.recycle()
    }

    fun startFocus(event: MotionEvent) {
        val point = Point(event.rawX.toInt(), event.rawY.toInt())
        val params = image.layoutParams as LayoutParams
        params.topMargin = point.y - image.height / 2
        params.leftMargin = point.x - image.width / 2
        image.layoutParams = params
        image.visibility = VISIBLE
        image.setImageResource(focusingId)
        image.startAnimation(focusAnim)
        focusHandler.postDelayed(goneRunnable, 3500)
    }

    fun onFocusSuccess() {
        image.setImageResource(focusSucceedId)
        focusHandler.removeCallbacks(goneRunnable, null)
        focusHandler.postDelayed(goneRunnable, 1000)
    }

    fun onFocusFailed() {
        image.setImageResource(focusFailedId)
        focusHandler.removeCallbacks(goneRunnable, null)
        focusHandler.postDelayed(goneRunnable, 1000)
    }
}