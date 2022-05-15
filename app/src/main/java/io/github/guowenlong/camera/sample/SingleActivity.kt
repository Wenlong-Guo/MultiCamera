package io.github.guowenlong.camera.sample

import android.annotation.SuppressLint
import android.graphics.Point
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.github.guowenlong.multicamera.camera1.Camera1Renderer
import io.github.guowenlong.multicamera.utils.CameraUtils
import io.github.guowenlong.multicamera.widget.FocusLayout
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

/**
 * Description: 单个activity
 * Author:      郭文龙
 * Date:        2022/4/30 0:26
 * Gmail:       guowenlong20000@sina.com
 */
class SingleActivity : AppCompatActivity() {

    private val glSurfaceView: MultiGLSurfaceView by lazy { findViewById(R.id.glcamera) }

    private val focusView: FocusLayout by lazy { findViewById(R.id.fv_content) }

    private val callback by lazy {
        Camera.AutoFocusCallback { success, _ ->
            if (success) {
                focusView.onFocusSuccess()
            } else {
                focusView.onFocusFailed()
            }
            Log.e("guowenlong", "聚焦$success")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
        glSurfaceView.setMultiRenderer(Camera1Renderer(glSurfaceView))
        glSurfaceView.setScaleGestureDetector()
        glSurfaceView.setOnTouchListener { _, event ->
            glSurfaceView.getRenderer().getCamera()
                .focusOnRect(CameraUtils.getRect(event, 200, this), callback)
            focusView.startFocus(event)
            false
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.getRenderer().forceResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.getRenderer().forcePause()
    }
}