package com.github.siwarats.simplecam.surface

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.hardware.Camera
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.github.siwarats.simplecam.core.getBestAutoFocus
import com.github.siwarats.simplecam.core.getCorrectCameraRotation
import com.github.siwarats.simplecam.core.getDeviceRotation
import java.io.IOException

@SuppressLint("ViewConstructor")
open class CameraPreview(
    context: Context,
    protected val camera: Camera,
    private val callback: Camera.PreviewCallback?,
    private val autoFocusCallback: Camera.AutoFocusCallback? = null
) : SurfaceView(context), SurfaceHolder.Callback {

    init {
        camera.setDisplayOrientation(getCorrectCameraRotation(context))
        val params = camera.parameters
        params?.focusMode = camera.getBestAutoFocus()
        camera.parameters = params
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val params = camera.parameters
            if (params.maxNumFocusAreas > 0) {

                val rect = convertViewPositionToCameraPosition(
                    x = event.x.toInt(),
                    y = event.y.toInt(),
                    width = width,
                    height = height,
                    cameraPreviewSize = params.previewSize
                )

                val areas = mutableListOf(Camera.Area(rect, 1000))
                params.focusAreas = areas
//                params.meteringAreas = areas
                camera.parameters = params

                camera.autoFocus(autoFocusCallback)
            }
            return true
        }
        return super.onTouchEvent(event)
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

    /**
     * This method is convert view touch position to camera touch position.
     * Note : camera always landscape(90 degrees) but view can rotate in 0, 90, 270, 360
     * (reference by device rotation)
     *
     * @param x : View touch X
     * @param y : View touch Y
     * @param width : View width
     * @param height : View height
     * @param radius : Camera area
     * @param radius : Radius of rect
     * @param cameraPreviewSize : Preview size
     *
     * @return Correct camera position X and Y as Rect
     * Note : Use Rect.centerX() and Rect.centerY() to get correct X and Y
     */
    private fun convertViewPositionToCameraPosition(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        radius: Int = 50,
        cameraPreviewSize: Camera.Size
    ): Rect {

        val percentX: Float
        val percentY: Float

        when (getDeviceRotation(context)) {
            0 -> {
                percentX = y / height.toFloat()
                percentY = Math.abs(x - width) / width.toFloat()
            }
            90 -> {
                percentX = x / width.toFloat()
                percentY = y / height.toFloat()
            }
            180 -> {
                percentX = Math.abs(y - height) / height.toFloat()
                percentY = x / width.toFloat()
            }
            else -> {
                percentX = Math.abs(x - width) / width.toFloat()
                percentY = Math.abs(y - height) / height.toFloat()
            }
        }

        var cameraX = cameraPreviewSize.width.toFloat() * percentX
        var cameraY = cameraPreviewSize.height.toFloat() * percentY
        //Convert to unsigned integer range (example: [0-1000] to [-500,500])
        //Note: Camera center position is [0,0] but View is depend on Gravity.
        cameraX -= (cameraPreviewSize.width / 2)
        cameraY -= (cameraPreviewSize.height / 2)

        Log.e("CameraPreview", "input (x,y) -> $x,$y")
        Log.e("CameraPreview", "correct percent(x,y) -> $percentX,$percentY")
        Log.e("CameraPreview", "camera position(x,y) -> $cameraX,$cameraY")
        Log.e("CameraPreview", "------------------------------------")

        return Rect(
            (cameraX - radius).toInt(),
            (cameraY - radius).toInt(),
            (cameraX + radius).toInt(),
            (cameraY + radius).toInt()
        )
    }
}