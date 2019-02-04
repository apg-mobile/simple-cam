package com.github.siwarats.simplecam

import android.hardware.Camera
import android.view.Gravity
import android.widget.FrameLayout
import com.github.siwarats.simplecam.const.PreviewMode
import com.github.siwarats.simplecam.core.getCameraInstance
import com.github.siwarats.simplecam.surface.CameraPreview
import com.github.siwarats.simplecam.surface.CenterCropCameraPreview
import com.github.siwarats.simplecam.surface.CenterInsideCameraPreview
import com.github.siwarats.simplecam.surface.SquareCameraPreview

class SimpleCam private constructor() {

    var target: FrameLayout? = null
        private set

    var camera: Camera? = null
        private set

    var cameraPreview: CameraPreview? = null
        private set

    var previewMode: PreviewMode? = null
        private set

    var previewCallback: PreviewCallback? = null
        private set

    fun startInto(target: FrameLayout?): CameraPreview {
        this.target = target
                ?: throw NullPointerException("target must not be null.")

        val cam = getCameraInstance()
            ?: throw IllegalStateException("No camera or missing permission or camera in use.")

        camera = cam

        val cameraCallback = Camera.PreviewCallback { data, _ ->
            if (data != null && data.isNotEmpty()) {
                camera?.also { previewCallback?.onPreviewFrame(data, it) }
            }
        }

        val context = target.context
        val camPreview = when (previewMode) {
            PreviewMode.CENTER_CROP -> CenterCropCameraPreview(context, cam, cameraCallback)
            PreviewMode.CENTER_INSIDE -> CenterInsideCameraPreview(context, cam, cameraCallback)
            else -> SquareCameraPreview(context, cam, cameraCallback)
        }
        cameraPreview = camPreview

        target.addView(cameraPreview)
        val previewParams = cameraPreview?.layoutParams as? FrameLayout.LayoutParams
        previewParams?.gravity = Gravity.CENTER

        return camPreview
    }

    fun release() {
        camera?.setPreviewCallback(null)
        camera?.release()
        previewCallback?.onCameraRelease()

        target?.removeView(cameraPreview)

        camera = null
        cameraPreview = null
        target = null
        previewMode = null
        previewCallback = null
    }

    class Builder {

        private val simpleCam = SimpleCam()

        init {
            simpleCam.previewMode = PreviewMode.SQUARE
        }

        fun setPreviewMode(pm: PreviewMode): Builder {
            simpleCam.previewMode = pm
            return this
        }

        fun setCallback(c: PreviewCallback): Builder {
            simpleCam.previewCallback = c
            return this
        }

        fun build(): SimpleCam {
            return simpleCam
        }
    }
}