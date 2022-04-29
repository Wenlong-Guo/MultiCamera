package io.github.guowenlong.camera.sample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

/**
 * Description: 单个activity
 * Author:      郭文龙
 * Date:        2022/4/30 0:26
 * Gmail:       guowenlong20000@sina.com
 */
class SingleActivity : AppCompatActivity() {

    private val glSurfaceView: MultiGLSurfaceView by lazy { findViewById(R.id.glcamera) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.forceResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.forcePause()
    }

}