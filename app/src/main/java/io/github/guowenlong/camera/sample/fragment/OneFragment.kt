package io.github.guowenlong.camera.sample.fragment

import android.util.Log
import android.view.View
import android.widget.Button
import io.github.guowenlong.camera.sample.R
import io.github.guowenlong.camera.sample.base.BaseFragment
import io.github.guowenlong.multicamera.camera1.Camera1Renderer
import io.github.guowenlong.multicamera.filter.CoolMagicFilter
import io.github.guowenlong.multicamera.filter.WarmMagicFilter
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/4/27 18:14
 * Gmail:       guowenlong20000@sina.com
 */
class OneFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_one

    private lateinit var cameraView: MultiGLSurfaceView
    private var isShow = true
    private var isCool = true

    companion object {
        fun instance(): OneFragment {
            return OneFragment()
        }
    }

    override fun init(view: View) {
        cameraView = view.findViewById(R.id.glcamera)
        cameraView.setMultiRenderer(Camera1Renderer(cameraView))
        view.findViewById<Button>(R.id.btn_one).setOnClickListener {
            if (isCool) {
                cameraView.getRenderer().showMagicFilter(WarmMagicFilter(this.requireContext()))
            } else {
                cameraView.getRenderer().showMagicFilter(CoolMagicFilter(this.requireContext()))
            }
            isCool = !isCool
        }
    }

    override fun onResume() {
        super.onResume()
        if (isShow) cameraView.getRenderer().forceResume()
        Log.e("one", "onResume")
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
        Log.e("one", "onHiddenChanged")
    }
}