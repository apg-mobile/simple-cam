package com.github.siwarats.simplecam.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.siwarats.simplecam.SimpleCam
import com.github.siwarats.simplecam.const.PreviewMode
import kotlinx.android.synthetic.main.activity_simple_preview.*

class SimplePreviewActivity : CameraPermissionActivity() {

    private val mode by lazy {
        intent?.getStringExtra("mode")
            ?.let {
                PreviewMode.valueOf(it)
            }
            ?: PreviewMode.SQUARE
    }
    private var simpleCam: SimpleCam? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_preview)
    }

    override fun onResume() {
        super.onResume()

        simpleCam = SimpleCam.Builder()
            .setPreviewMode(mode)
            .build()

        simpleCam?.startInto(flPreview)
    }

    override fun onPause() {
        simpleCam?.release()
        super.onPause()
    }

    companion object {
        fun createIntent(context: Context?, mode: PreviewMode) =
            Intent(context, SimplePreviewActivity::class.java)
                .apply {
                    putExtra("mode", mode.name)
                }
    }
}