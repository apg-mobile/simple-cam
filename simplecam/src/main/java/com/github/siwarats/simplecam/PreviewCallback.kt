package com.github.siwarats.simplecam

import android.hardware.Camera

interface PreviewCallback {
    fun onPreviewFrame(data: ByteArray, camera: Camera)
    fun onCameraRelease()
}