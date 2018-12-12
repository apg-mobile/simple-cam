package com.github.siwarats.simplecam.zxing

import android.os.Bundle
import android.os.Looper

class ZXingThread(
    private val decoder: ZXingController,
    private val callback: Callback
) : Thread() {

    var handler: ZXingHandler? = null

    override fun run() {
        super.run()
        Looper.prepare()
        handler = ZXingHandler(decoder, callback)
        Looper.loop()
    }

    fun sendMessage(previewData: ByteArray, width: Int, height: Int) {
        if (handler?.status == ZXingHandler.Status.IDLE) {
            handler?.obtainMessage()
                ?.apply {
                    data = Bundle()
                        .apply {
                            putByteArray("previewData", previewData)
                            putInt("width", width)
                            putInt("height", height)
                        }
                }
                ?.also {
                    handler?.sendMessage(it)
                }
        }
    }
}