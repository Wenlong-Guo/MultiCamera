package io.github.guowenlong.camera.mylibrary

import android.hardware.Camera
import android.util.Size
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.lifecycle.LifecycleOwner

/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/4/24 16:57
 * Gmail:       guowenlong20000@sina.com
 */
object CameraHelper {
    val currentFacing = CameraX.LensFacing.BACK

    fun init(lifecycleOwner: LifecycleOwner, listener: Preview.OnPreviewOutputUpdateListener) {
        CameraX.bindToLifecycle(lifecycleOwner, getPreview(listener))
    }

    private fun getPreview(listener: Preview.OnPreviewOutputUpdateListener): Preview {
        val previewConfig = PreviewConfig.Builder()
            .setTargetResolution(Size(1280, 720))
            .setLensFacing(currentFacing)
            .build()
        val preview = Preview(previewConfig)
        preview.onPreviewOutputUpdateListener = listener
        return preview
    }
}