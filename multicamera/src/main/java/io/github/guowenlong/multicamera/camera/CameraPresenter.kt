package io.github.guowenlong.multicamera.camera

import android.util.Size
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.lifecycle.LifecycleOwner

/**
 * Description: 相机的代理类
 * Author:      郭文龙
 * Date:        2022/4/26 16:18
 * Gmail:       guowenlong20000@sina.com
 */
class CameraPresenter {

    private val currentFacing = CameraX.LensFacing.BACK

    fun init(lifecycleOwner: LifecycleOwner, listener: Preview.OnPreviewOutputUpdateListener) {
        CameraX.bindToLifecycle(lifecycleOwner, getPreview(listener))
    }

    private fun getPreview(listener: Preview.OnPreviewOutputUpdateListener): Preview {
        val previewConfig = PreviewConfig.Builder()
            .setTargetResolution(Size(640 * 4, 480 * 4))
            .setLensFacing(currentFacing)
            .build()
        val preview = Preview(previewConfig)
        preview.onPreviewOutputUpdateListener = listener
        return preview
    }
}