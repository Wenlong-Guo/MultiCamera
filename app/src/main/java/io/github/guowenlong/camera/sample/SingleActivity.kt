package io.github.guowenlong.camera.sample

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.github.guowenlong.multicamera.camera1.Camera1Renderer
import io.github.guowenlong.multicamera.utils.CameraUtils
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

/**
 * Description: 单个activity
 * Author:      郭文龙
 * Date:        2022/4/30 0:26
 * Gmail:       guowenlong20000@sina.com
 */
class SingleActivity : AppCompatActivity() {

    private val glSurfaceView: MultiGLSurfaceView by lazy { findViewById(R.id.glcamera) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
        glSurfaceView.setMultiRenderer(Camera1Renderer(glSurfaceView))
        glSurfaceView.setScaleGestureDetector()
        glSurfaceView.setOnTouchListener { _, event ->
            glSurfaceView.getRenderer().getCamera().focusOnRect(CameraUtils.getRect(event,200,this))
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