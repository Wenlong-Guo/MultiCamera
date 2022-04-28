package io.github.guowenlong.camera.sample

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.compose.animation.core.RepeatMode
import io.github.guowenlong.camera.sample.fragment.OneFragment
import io.github.guowenlong.camera.sample.fragment.TwoFragment
import io.github.guowenlong.multicamera.camera.TakePictureListener
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragments()
        findViewById<Button>(R.id.btn_left).setOnClickListener {
            turnLeft()
        }
        findViewById<Button>(R.id.btn_right).setOnClickListener {
            turnRight()
        }
    }

    val oneFragment by lazy { OneFragment.instance() }
    val twoFragment by lazy { TwoFragment.instance() }

    private fun initFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_content, oneFragment, "one")
            .add(R.id.fl_content, twoFragment, "two")
            .hide(twoFragment)
            .show(oneFragment)
            .commit()

    }



    private fun turnLeft() {
        supportFragmentManager.beginTransaction()
            .hide(twoFragment)
            .show(oneFragment)
            .commitNowAllowingStateLoss()
    }

    private fun turnRight() {
        supportFragmentManager.beginTransaction()
            .hide(oneFragment)
            .show(twoFragment)
            .commitNowAllowingStateLoss()
    }
}