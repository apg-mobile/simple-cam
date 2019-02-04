package com.github.siwarats.simplecam.zxing

interface Callback {
    fun onReceivedCode(result: ResultObject)
}