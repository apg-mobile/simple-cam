package com.github.siwarats.simplecam.extension

import android.app.Activity
import android.content.Context
import android.view.Surface
import android.view.View

fun Context.isDevicePortrait(): Boolean? {
    if (this is Activity) {
        val rotation = windowManager?.defaultDisplay?.rotation ?: return null
        return rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180
    }
    return null
}

fun View.isDevicePortrait(): Boolean? {
    return context?.isDevicePortrait()
}