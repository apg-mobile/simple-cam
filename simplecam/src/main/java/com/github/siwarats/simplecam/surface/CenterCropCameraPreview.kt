package com.github.siwarats.simplecam.surface

import android.content.Context
import android.hardware.Camera
import com.github.siwarats.simplecam.extension.isDevicePortrait
import com.github.siwarats.simplecam.surface.CameraPreview

open class CenterCropCameraPreview(
        context: Context,
        camera: Camera,
        callback: Camera.PreviewCallback?
) : CameraPreview(context, camera, callback) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val preview = camera.parameters?.previewSize ?: return
        val isDevicePortrait = isDevicePortrait() ?: return

        val previewWidth = if (isDevicePortrait) preview.height else preview.width
        val previewHeight = if (isDevicePortrait) preview.width else preview.height
        val previewRatio = previewWidth.toDouble() / previewHeight.toDouble()

        val originalWidth = measuredWidth
        val originalHeight = measuredHeight

        var newWidth = 0.0
        var newHeight = 0.0

        while (newWidth < originalWidth || newHeight < originalHeight) {
            newWidth += previewRatio
            newHeight++
        }

        setMeasuredDimension(newWidth.toInt(), newHeight.toInt())
    }
}