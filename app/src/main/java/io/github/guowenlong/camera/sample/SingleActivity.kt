package io.github.guowenlong.camera.sample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.github.guowenlong.camera.camera.Camera2Presenter
import io.github.guowenlong.camera.widget.MultiGLSurfaceView
import io.github.guowenlong.camera.widget.MultiRenderer

//import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

/**
 * Description: 单个activity
 * Author:      郭文龙
 * Date:        2022/4/30 0:26
 * Gmail:       guowenlong20000@sina.com
 */
class SingleActivity : AppCompatActivity() {

    private val glSurfaceView: MultiGLSurfaceView by lazy { findViewById(R.id.glcamera) }

    private val turn : Button by lazy { findViewById(R.id.btn_toggle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
//        val camera2 = Camera2Presenter(this)
//        val renderer = glSurfaceView.getRenderer()
//        renderer.bindCamera(camera2)
        turn.setOnClickListener {
//            glSurfaceView.switchCamera()
//            renderer.forceResume()
        }
    }

    override fun onResume() {
        super.onResume()
//        glSurfaceView.forceResume()
    }

    override fun onPause() {
        super.onPause()
//        glSurfaceView.forcePause()
    }

}