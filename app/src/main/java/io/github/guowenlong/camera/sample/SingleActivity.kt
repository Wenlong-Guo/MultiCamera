package io.github.guowenlong.camera.sample

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.guowenlong.camera.sample.fragment.FilterAdapter
import io.github.guowenlong.multicamera.camera1.Camera1Renderer
import io.github.guowenlong.multicamera.camera1.TakeGLPictureListener
import io.github.guowenlong.multicamera.filter.*
import io.github.guowenlong.multicamera.utils.CameraUtils
import io.github.guowenlong.multicamera.widget.FocusLayout
import io.github.guowenlong.multicamera.widget.MultiGLSurfaceView

/**
 * Description: 单个activity
 * Author:      郭文龙
 * Date:        2022/4/30 0:26
 * Gmail:       guowenlong20000@sina.com
 */
class SingleActivity : AppCompatActivity() {

    private val glSurfaceView: MultiGLSurfaceView by lazy { findViewById(R.id.glcamera) }

    private val focusView: FocusLayout by lazy { findViewById(R.id.fv_content) }

    private val rvFilter: RecyclerView by lazy { findViewById(R.id.rv_filter) }

    private val ivTakePicture: ImageView by lazy { findViewById(R.id.iv_take_picture) }

    private val ivPreview: ImageView by lazy { findViewById(R.id.iv_photo) }

    private val ivSwitch: ImageView by lazy { findViewById(R.id.iv_switch) }

    private val ivFilter: ImageView by lazy { findViewById(R.id.iv_filter) }

    private var isFilterShow  = false

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
                    "温暖" -> WarmMagicFilter(this@SingleActivity)
                    "寒冷" -> CoolMagicFilter(this@SingleActivity)
                    "黑猫" -> BlackCatMagicFilter(this@SingleActivity)
                    "白猫" -> WhiteCatMagicFilter(this@SingleActivity)
                    "黑白" -> BlackWhiteMagicFilter(this@SingleActivity)
                    "日出" -> SunriseMagicFilter(this@SingleActivity)
                    "日落" -> SunsetMagicFilter(this@SingleActivity)
                    "浪漫" -> RomanceMagicFilter(this@SingleActivity)
                    "樱花" -> SakuraMagicFilter(this@SingleActivity)
                    "健康" -> HealthyMagicFilter(this@SingleActivity)
                    "拿铁" -> LatteMagicFilter(this@SingleActivity)
                    "安静" -> QuietMagicFilter(this@SingleActivity)
                    "复古" -> AntiqueMagicFilter(this@SingleActivity)
                    else -> OriginFilter(this@SingleActivity)
                }
                glSurfaceView.getRenderer().showMagicFilter(filter)
            }
        }
    }
    /**
     * 聚焦回调
     */
    private val callback by lazy {
        Camera.AutoFocusCallback { success, _ ->
            if (success) {
                focusView.onFocusSuccess()
            } else {
                focusView.onFocusFailed()
            }
            Log.e("guowenlong", "聚焦$success")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)

        /**
         * 设置Renderer 直接预览
         */
        glSurfaceView.setMultiRenderer(Camera1Renderer(glSurfaceView))

        /**
         * 设置触摸手势 放大缩小
         */
        glSurfaceView.setScaleGestureDetector()

        /**
         * 手动聚焦
         */
        glSurfaceView.setOnTouchListener { _, event ->
            glSurfaceView.getRenderer().getCamera()
                .focusOnRect(CameraUtils.getRect(event, 200, this), callback)
            focusView.startFocus(event)
            false
        }

        /**
         * 切换摄像头
         */
        ivSwitch.setOnClickListener {
            glSurfaceView.getRenderer().switchCamera()
        }

        /**
         * 设置滤镜是否显示
         */
        ivFilter.setOnClickListener {
            rvFilter.visibility = if (isFilterShow) View.GONE else View.VISIBLE
            isFilterShow = !isFilterShow
        }

        /**
         * 拍照
         */
        ivTakePicture.setOnClickListener {

            /**
             * 无声音拍照(像素低)
             */
            glSurfaceView.getRenderer().takeOriginPicture(
                object : TakeGLPictureListener {
                    override fun onCollect(bitmap: Bitmap) {
                        ivPreview.setImageBitmap(bitmap)
                    }
                }
            )

//            /**
//             * 有声音拍照(像素高)(无滤镜)
//             */
//            glSurfaceView.getRenderer().takeOriginPicture(listener = object : TakeCameraPictureListener {
//                override fun onCollect(previewBitmap: Bitmap, bytes: ByteArray, camera: ICamera) {
//                    ivPreview.setImageBitmap(previewBitmap)
//                    camera.startPreview()
//                }
//            })
        }

        /**
         * 初始化滤镜列表
         */
        initList()
    }

    private fun initList() {
        val layoutManager = LinearLayoutManager(this@SingleActivity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL;
        rvFilter.layoutManager = layoutManager
        rvFilter.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        /**
         * 必须保留
         */
        glSurfaceView.getRenderer().forceResume()
    }

    override fun onPause() {
        super.onPause()
        /**
         * 必须保留
         */
        glSurfaceView.getRenderer().forcePause()
    }
}