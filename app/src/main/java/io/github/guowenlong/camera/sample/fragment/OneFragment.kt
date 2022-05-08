//package io.github.guowenlong.camera.sample.fragment
//
//import android.util.Log
//import android.view.View
//import android.widget.Button
//import io.github.guowenlong.camera.sample.R
//import io.github.guowenlong.camera.sample.base.BaseFragment
//import io.github.guowenlong.multicamera.filter.CoolMagicFilter
//import io.github.guowenlong.multicamera.filter.WarmMagicFilter
//import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView
//
///**
// * Description:
// * Author:      郭文龙
// * Date:        2022/4/27 18:14
// * Gmail:       guowenlong20000@sina.com
// */
//class OneFragment : BaseFragment() {
//
//    override val layoutId = R.layout.fragment_one
//
//    private lateinit var cameraView: MultiGLSurfaceView
//    private var isShow = true
//    private var isCool = true
//
//    companion object {
//        fun instance(): OneFragment {
//            return OneFragment()
//        }
//    }
//
//    override fun init(view: View) {
//        cameraView = view.findViewById(R.id.glcamera)
//        view.findViewById<Button>(R.id.btn_one).setOnClickListener {
//            if (isCool) {
//                cameraView.showMagicFilter(WarmMagicFilter(this.requireContext()))
//            } else {
//                cameraView.showMagicFilter(CoolMagicFilter(this.requireContext()))
//            }
//            isCool = !isCool
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (isShow) cameraView.forceResume()
//        Log.e("one", "onResume")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        cameraView.forcePause()
//    }
//
//    override fun onHiddenChanged(hidden: Boolean) {
//        super.onHiddenChanged(hidden)
//        isShow = !hidden
//        if (hidden) {
//            cameraView.forcePause()
//        } else {
//            cameraView.forceResume()
//        }
//        Log.e("one", "onHiddenChanged")
//    }
//}