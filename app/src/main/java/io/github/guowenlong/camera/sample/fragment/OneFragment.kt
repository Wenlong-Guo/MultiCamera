package io.github.guowenlong.camera.sample.fragment

import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.guowenlong.camera.sample.R
import io.github.guowenlong.camera.sample.base.BaseFragment
import io.github.guowenlong.multicamera.camera1.Camera1Renderer
import io.github.guowenlong.multicamera.filter.*
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
    private lateinit var filterRv: RecyclerView
    private var isShow = true
    private var isCool = true

    private val list = mutableListOf(
        "无滤镜",
        "温暖",
        "寒冷",
        "黑猫",
        "白猫",
        "黑白",
        "日出",
        "日落",
        "健康",
        "樱花",
        "浪漫",
        "拿铁",
        "安静",
        "复古",
    )

    private val adapter by lazy {
        object : FilterAdapter(list) {
            override fun onItemClickListener(position: Int, title: String) {
                val filter: BaseFilter = when (title) {
                    "温暖" -> WarmMagicFilter(requireContext())
                    "寒冷" -> CoolMagicFilter(requireContext())
                    "黑猫" -> BlackCatMagicFilter(requireContext())
                    "白猫" -> WhiteCatMagicFilter(requireContext())
                    "黑白" -> BlackWhiteMagicFilter(requireContext())
                    "日出" -> SunriseMagicFilter(requireContext())
                    "日落" -> SunsetMagicFilter(requireContext())
                    "浪漫" -> RomanceMagicFilter(requireContext())
                    "樱花" -> SakuraMagicFilter(requireContext())
                    "健康" -> HealthyMagicFilter(requireContext())
                    "拿铁" -> LatteMagicFilter(requireContext())
                    "安静" -> QuietMagicFilter(requireContext())
                    "复古" -> AntiqueMagicFilter(requireContext())
                    else -> OriginFilter(requireContext())
                }
                cameraView.getRenderer().showMagicFilter(filter)
            }
        }
    }

    companion object {
        fun instance(): OneFragment {
            return OneFragment()
        }
    }

    override fun init(view: View) {
        cameraView = view.findViewById(R.id.glcamera)
        filterRv = view.findViewById(R.id.rv_filter)
        cameraView.setMultiRenderer(Camera1Renderer(cameraView))
        view.findViewById<Button>(R.id.btn_one).setOnClickListener {
            if (isCool) {
                cameraView.getRenderer().showMagicFilter(BlackCatMagicFilter(this.requireContext()))
            } else {
                cameraView.getRenderer().showMagicFilter(WhiteCatMagicFilter(this.requireContext()))
            }
            isCool = !isCool
        }
        initList()
    }

    private fun initList() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL;
        filterRv.layoutManager = layoutManager
        filterRv.adapter = adapter
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