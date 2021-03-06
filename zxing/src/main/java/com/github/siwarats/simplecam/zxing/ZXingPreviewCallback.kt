package com.github.siwarats.simplecam.zxing

import android.hardware.Camera
import com.github.siwarats.simplecam.PreviewCallback

open class ZXingPreviewCallback(
    private val callback: Callback
) : PreviewCallback {

    private var thread: ZXingThread? = null

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        if (thread == null) {
            val controller = ZXingController()
            thread = ZXingThread(controller, callback)
                .also {
                    it.start()
                }
        }

        val previewSize = camera.parameters?.previewSize
        if (previewSize != null) {
            val height = previewSize.height
            val width = previewSize.width

            thread?.sendMessage(data, width, height)
        }
    }

    override fun onCameraRelease() {
        thread?.interrupt()
        thread = null
    }

}