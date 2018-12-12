package com.github.siwarats.simplecam.zxing

import android.os.Handler
import android.os.Looper
import android.os.Message

class ZXingHandler(
    private val decoder: ZXingController,
    private val callback: com.github.siwarats.simplecam.zxing.Callback
) : Handler() {

    enum class Status {
        IDLE,
        PENDING
    }

    var status = Status.IDLE

    override fun handleMessage(msg: Message?) {
        status = Status.PENDING

        val data = msg?.data
        if (data != null) {
            val previewData = data.getByteArray("previewData")
            val width = data.getInt("width")
            val height = data.getInt("height")

            val result = decoder.decode(
                previewData,
                width,
                height
            )

            if (result != null) {
                Handler(Looper.getMainLooper())
                    .post {
                        callback.onReceivedCode(result)
                    }
            }
        }

        status = Status.IDLE
    }
}