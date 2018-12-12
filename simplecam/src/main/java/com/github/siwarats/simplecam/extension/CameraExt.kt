package com.github.siwarats.simplecam.extension

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.view.Surface

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

    return if (supportFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
    } else if (supportFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
        Camera.Parameters.FOCUS_MODE_AUTO
    } else {
        params.focusMode
    }
}

/**
 * https://developer.android.com/reference/android/hardware/Camera#setDisplayOrientation(int)
 */
fun getCorrectDegree(context: Context): Int {
    val rotation = context
        .let {
            it as? Activity
        }
        ?.windowManager
        ?.defaultDisplay
        ?.rotation!! //or throw NullPointerException()

    val info = Camera.CameraInfo()
    Camera.getCameraInfo(0, info)

    val degrees = when (rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        else -> 270 //Surface.ROTATION_270
    }

    return if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        (360 - ((info.orientation + degrees) % 360)) % 360
    } else {
        (info.orientation - degrees + 360) % 360
    }
}