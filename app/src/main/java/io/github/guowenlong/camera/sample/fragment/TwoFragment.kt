package io.github.guowenlong.camera.sample.fragment

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.compose.animation.core.RepeatMode
import io.github.guowenlong.camera.sample.R
import io.github.guowenlong.camera.sample.base.BaseFragment
import io.github.guowenlong.multicamera.camera1.Camera1Renderer
import io.github.guowenlong.multicamera.camera1.TakeCameraPictureListener
import io.github.guowenlong.multicamera.camera1.TakeGLPictureListener
import io.github.guowenlong.multicamera.core.ICamera
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/4/27 18:21
 * Gmail:       guowenlong20000@sina.com
 */
class TwoFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_two

    companion object {
        fun instance(): TwoFragment {
            return TwoFragment()
        }
    }

    private lateinit var cameraView: MultiGLSurfaceView
    private lateinit var picture: ImageView
    private var isShow = false
    override fun init(view: View) {
        cameraView = view.findViewById(R.id.glcamera)
        cameraView.setMultiRenderer(Camera1Renderer(cameraView))
        picture = view.findViewById<ImageView>(R.id.iv_picture)
        view.findViewById<Button>(R.id.btn).setOnClickListener {
            cameraView.getRenderer().switchCamera()
        }

        view.findViewById<Button>(R.id.btn_picture).setOnClickListener {
            /**
             * openGL的一帧
             */
            cameraView.getRenderer().takeOriginPicture(
                object : TakeGLPictureListener {
                    override fun onCollect(bitmap: Bitmap) {
                        picture.setImageBitmap(bitmap)
                    }
                }
            )
        }
        view.findViewById<Button>(R.id.btn_picture2).setOnClickListener {
            /**
             * 原生相机的拍照
             */
            cameraView.getRenderer().takeOriginPicture(listener = object : TakeCameraPictureListener {
                override fun onCollect(previewBitmap: Bitmap, bytes: ByteArray, camera: ICamera) {
                    picture.setImageBitmap(previewBitmap)
                    camera.startPreview()
                }
            })
        }
        animation(view.findViewById<Button>(R.id.btn))
        view.findViewById<Button>(R.id.btn_two).setOnClickListener {

        }
    }

    private fun animation(view: View) {
        val rotateAnimation1 =
            RotateAnimation(0f, 360f, view.width.toFloat() / 2, view.height.toFloat() / 2)
        rotateAnimation1.fillAfter = true
        rotateAnimation1.duration = 1000
        rotateAnimation1.repeatCount = -1
        rotateAnimation1.repeatMode = RepeatMode.Restart.ordinal
        view.startAnimation(rotateAnimation1)
    }

    override fun onResume() {
        super.onResume()
        if (isShow) cameraView.getRenderer().forceResume()
        Log.e("two", "onResume")
    }

    override fun onPause() {
        super.onPause()
        cameraView.getRenderer().forcePause()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isShow = !hidden
        if (hidden) {
            cameraView.getRenderer().forcePause()
        } else {
            cameraView.getRenderer().forceResume()
        }
        Log.e("two", "onHiddenChanged")
    }
}