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
import io.github.guowenlong.multicamera.camera.TakePictureListener
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cameraView = findViewById<MultiGLSurfaceView>(R.id.glcamera)
        val picture = findViewById<ImageView>(R.id.iv_picture)
        findViewById<Button>(R.id.btn).setOnClickListener {
            cameraView.switchCamera()
            Log.e("activity", "width :${cameraView.width}")
            Log.e("activity", "height :${cameraView.height}")
        }

        findViewById<Button>(R.id.btn_picture).setOnClickListener {
            cameraView.takePicture(object : TakePictureListener {
                override fun onCollect(bitmap: Bitmap) {
                    Log.e("activity", "bitmap :$bitmap")
                    picture.setImageBitmap(bitmap)
                }
            })
            Log.e("activity", "width :${cameraView.width}")
            Log.e("activity", "height :${cameraView.height}")
        }
        animation(findViewById<Button>(R.id.btn))
    }

    private fun animation(view: View) {
        val rotateAnimation1 =
            RotateAnimation(0f, 360f, view.width.toFloat() / 2, view.height.toFloat() / 2);
        rotateAnimation1.fillAfter = true;
        rotateAnimation1.duration = 1000
        rotateAnimation1.repeatCount = -1
        rotateAnimation1.repeatMode = RepeatMode.Restart.ordinal
        view.startAnimation(rotateAnimation1);
    }

//    override fun onResume() {
//        super.onResume()
//        findViewById<MultiSurfaceView>(R.id.glcamera).switchCamera()
//        findViewById<MultiSurfaceView>(R.id.glcamera).switchCamera()
//
//    }
}