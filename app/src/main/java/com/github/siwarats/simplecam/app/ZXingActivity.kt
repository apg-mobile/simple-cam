package com.github.siwarats.simplecam.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.siwarats.simplecam.SimpleCam
import com.github.siwarats.simplecam.const.PreviewMode
import com.github.siwarats.simplecam.zxing.Callback
import com.github.siwarats.simplecam.zxing.ZXingPreviewCallback
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_zxing.*

class ZXingActivity : CameraPermissionActivity(), Callback {

    private var simpleCam: SimpleCam? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zxing)
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

    override fun onReceivedCode(result: Result) {
        tvResult?.text = "${result.text} (${result.barcodeFormat.name})"
    }

    companion object {
        fun createIntent(context: Context?) = Intent(context, ZXingActivity::class.java)
    }
}