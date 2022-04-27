package io.github.guowenlong.camera.sample.fragment

import android.view.View
import io.github.guowenlong.camera.sample.R
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/4/27 18:14
 * Gmail:       guowenlong20000@sina.com
 */
class OneFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_one

    private lateinit var cameraView :MultiGLSurfaceView

    companion object {
        fun instance(): OneFragment {
            return OneFragment()
        }
    }

    override fun init(view: View) {
        cameraView = view.findViewById(R.id.glcamera)
    }

    fun open(){
        cameraView.forceResume()
    }
}