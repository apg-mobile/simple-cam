package com.github.siwarats.simplecam.zxing

import com.google.zxing.Result

interface Callback {
    fun onReceivedCode(result: Result)
}