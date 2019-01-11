package com.github.siwarats.simplecam.extension

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.view.Surface
import android.view.View

fun getDeviceRotation(context: Context): Int {
    val rotation = context
        .let {
            it as? Activity
        }
        ?.windowManager
        ?.defaultDisplay
        ?.rotation!! //or throw NullPointerException()

    val info = Camera.CameraInfo()
    Camera.getCameraInfo(0, info)

    return when (rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        else -> 270 //Surface.ROTATION_270
    }
}

fun Context.isDevicePortrait(): Boolean {
    val degree = getDeviceRotation(this)
    return degree == 0 || degree == 180
}

fun View.isDevicePortrait(): Boolean? {
    return context?.isDevicePortrait()
}