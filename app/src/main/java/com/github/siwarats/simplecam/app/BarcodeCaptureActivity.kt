package com.github.siwarats.simplecam.app

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.github.siwarats.simplecam.SimpleCam
import com.github.siwarats.simplecam.const.PreviewMode
import com.github.siwarats.simplecam.core.getDeviceRotation
import com.github.siwarats.simplecam.core.rotateBitmap
import com.github.siwarats.simplecam.zxing.Callback
import com.github.siwarats.simplecam.zxing.ResultObject
import com.github.siwarats.simplecam.zxing.ZXingPreviewCallback
import kotlinx.android.synthetic.main.activity_barcode_capture.*
import kotlinx.android.synthetic.main.dialog_fragment_post_view.*
import java.io.ByteArrayOutputStream

class BarcodeCaptureActivity : CameraPermissionActivity(), Callback {

    private var simpleCam: SimpleCam? = null
    private var isCapture = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_capture)

        button?.setOnClickListener { _ ->
            isCapture = true
        }

        imageView2?.setOnClickListener { _ ->
            GalleryActivity.createIntent(this)
                .also { startActivity(it) }
        }
    }

    override fun onResume() {
        super.onResume()

        simpleCam = SimpleCam.Builder()
            .setPreviewMode(PreviewMode.CENTER_CROP)
            .setCallback(ZXingPreviewCallback(this))
            .build()

        simpleCam?.startInto(flPreview)
    }

    override fun onPause() {
        simpleCam?.release()
        super.onPause()
    }

    override fun onReceivedCode(result: ResultObject) {
        tvResult?.text = "${result.result.text} (${result.result.barcodeFormat.name})"

        if (isCapture) {

            isCapture = false

            val yuv = YuvImage(
                result.byteArray,
                ImageFormat.NV21,
                result.width,
                result.height,
                null
            )

            val baos = ByteArrayOutputStream()

            yuv.compressToJpeg(
                Rect(0, 0, result.width, result.height),
                100,
                baos
            )

            val horizontalBitmap = BitmapFactory.decodeByteArray(
                baos.toByteArray(),
                0,
                baos.size()
            )

            val degrees = when (getDeviceRotation(this)) {
                0 -> 90
                90 -> 0
                180 -> -90
                270 -> 180
                else -> 0
            }
            val rotateBitmap = horizontalBitmap.rotateBitmap(degrees.toFloat())

            ResultDialogFragment
                .newInstance(
                    bitmap = rotateBitmap,
                    trackingId = result.result.text
                )
                .also {
                    it.show(supportFragmentManager, null)
                }
        }
    }

    companion object {
        fun createIntent(context: Context?) = Intent(context, BarcodeCaptureActivity::class.java)
    }

    class ResultDialogFragment : DialogFragment() {

        private val trackingId by lazy { arguments!!.getString("trackingId") }
        private val bitmap by lazy { arguments!!.getParcelable<Bitmap>("bitmap") }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.dialog_fragment_post_view, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            imgPostView?.setImageBitmap(bitmap)
            tvQr?.text = trackingId
            button2?.setOnClickListener {
               if(BarcodeStorageController().saveImage(context!!, bitmap, trackingId)) {
                   dismiss()
               } else {
                   Toast.makeText(context!!, "Save ไม่ได้", Toast.LENGTH_SHORT).show()
               }
            }
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            dialog?.window?.attributes = dialog?.window?.attributes?.apply {
                height = WindowManager.LayoutParams.WRAP_CONTENT
                width = WindowManager.LayoutParams.MATCH_PARENT
            }

            dialog?.setOnCancelListener {
                it.dismiss()
            }
        }

        companion object {
            fun newInstance(bitmap: Bitmap, trackingId: String): ResultDialogFragment = ResultDialogFragment()
                .apply {
                    arguments = Bundle()
                        .apply {
                            putString("trackingId", trackingId)
                            putParcelable("bitmap", bitmap)
                        }
                }
        }
    }
}