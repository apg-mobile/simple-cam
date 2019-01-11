package com.github.siwarats.simplecam.extension

import android.content.Context
import android.hardware.Camera

fun getCameraInstance(): Camera? {
    return try {
        Camera.open()
    } catch (e: Exception) {
        null
    }
}

fun Camera.getBestAutoFocus(): String {
    val params = parameters!! // or thrown NullPointerException

    val supportFocusModes = params.supportedFocusModes ?: mutableListOf()

    return if (supportFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
        Camera.Parameters.FOCUS_MODE_AUTO
    } else if (supportFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
    } else  {
        params.focusMode
    }
}

/**
 * https://developer.android.com/reference/android/hardware/Camera#setDisplayOrientation(int)
 */
fun getCorrectCameraRotation(context: Context): Int {
    val degrees = getDeviceRotation(context)

    val info = Camera.CameraInfo()
    Camera.getCameraInfo(0, info)

    return if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        (360 - ((info.orientation + degrees) % 360)) % 360
    } else {
        (info.orientation - degrees + 360) % 360
    }
}