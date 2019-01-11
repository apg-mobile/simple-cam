package com.github.siwarats.simplecam.surface

import android.content.Context
import android.hardware.Camera

open class SquareCameraPreview(
        context: Context,
        camera: Camera,
        callback: Camera.PreviewCallback?
) : CameraPreview(context, camera, callback) {

    init {
        val params = camera.parameters
        val supportPreviewSizes = params.supportedPreviewSizes
        val square = supportPreviewSizes.filter { it.height == it.width }.maxBy { it.height }
        if (square != null) {
            params.setPreviewSize(square.width, square.height)
            camera.parameters = params
        } else {
            //Not support square
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val originalWidth = measuredWidth
        val originalHeight = measuredHeight
        val max = Math.min(originalWidth, originalHeight)
        setMeasuredDimension(max, max)
    }
}