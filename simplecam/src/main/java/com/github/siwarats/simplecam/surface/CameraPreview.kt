package com.github.siwarats.simplecam.surface

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.github.siwarats.simplecam.extension.getBestAutoFocus
import com.github.siwarats.simplecam.extension.getCorrectDegree
import java.io.IOException

@SuppressLint("ViewConstructor")
open class CameraPreview(
    context: Context,
    protected val camera: Camera,
    private val callback: Camera.PreviewCallback?
) : SurfaceView(context), SurfaceHolder.Callback {

    init {
        camera.apply {
            setDisplayOrientation(getCorrectDegree(context))
            val params = parameters
            params?.focusMode = getBestAutoFocus()
            parameters = params
        }
    }

    private var mHolder: SurfaceHolder = holder.apply {
        addCallback(this@CameraPreview)
        setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        mHolder.surface ?: return

        try {
            camera.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        camera.apply {
            try {
                setPreviewDisplay(mHolder)
                setPreviewCallback(callback)
                startPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        camera.apply {
            try {
                setPreviewDisplay(holder)
                startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}