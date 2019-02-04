package com.github.siwarats.simplecam.zxing

import com.google.zxing.Result

class ResultObject(
    var byteArray: ByteArray,
    var result: Result,
    var width : Int,
    var height : Int
)