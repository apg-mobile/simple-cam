package com.github.siwarats.simplecam.core

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotateBitmap(angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}