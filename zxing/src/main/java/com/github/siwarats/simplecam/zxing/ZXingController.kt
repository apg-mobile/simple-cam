package com.github.siwarats.simplecam.zxing

import android.graphics.*
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.io.ByteArrayOutputStream
import java.util.*

class ZXingController(formats: MutableList<BarcodeFormat> = DEFAULT_HINTS) {

    companion object {
        val DEFAULT_HINTS = mutableListOf<BarcodeFormat>()
            .apply {
                add(BarcodeFormat.CODE_39)
                add(BarcodeFormat.CODE_93)
                add(BarcodeFormat.CODE_128)
                add(BarcodeFormat.QR_CODE)
            }
    }

    private val decoder: MultiFormatReader

    var isDecodeAllOrientation = true

    init {
        val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java)
            .apply {
                put(DecodeHintType.POSSIBLE_FORMATS, formats)
            }
        decoder = MultiFormatReader()
            .apply {
                setHints(hints)
            }
    }

    fun decode(data: ByteArray, width: Int, height: Int): ResultObject? {
        val result = if (isDecodeAllOrientation) {
            decodeAllOrientation(data, width, height)
        } else {
            decodeNormal(data, width, height)
        }
        return if (result != null) {
            ResultObject(
                byteArray = data,
                result = result,
                height = height,
                width = width
            )
        } else {
            null
        }
    }

    /**
     * This can scan all orientation with QRCode and horizontal with Barcode.
     */
    private fun decodeNormal(data: ByteArray, width: Int, height: Int): Result? {
        return try {
            decoder.decodeWithState(
                BinaryBitmap(
                    HybridBinarizer(
                        PlanarYUVLuminanceSource(
                            data,
                            width,
                            height,
                            0,
                            0,
                            width,
                            height,
                            false
                        )
                    )
                )
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * This can decoding by ignore orientation but take more time than decodeNormal() (Maybe 3-4 times).
     */
    private fun decodeAllOrientation(data: ByteArray, width: Int, height: Int): Result? {
        /*
         * Decoding
         */

        val yuv = YuvImage(
            data,
            ImageFormat.NV21,
            width,
            height, null
        )

        val baos = ByteArrayOutputStream()

        yuv.compressToJpeg(
            Rect(0, 0, width, height),
            100,
            baos
        )

        val horizontalBitmap = BitmapFactory.decodeByteArray(
            baos.toByteArray(),
            0,
            baos.size()
        )

        var result = decoding(horizontalBitmap)

        if (result == null) {
            val matrix = Matrix()
            matrix.setRotate(90f)

            val verticalBitmap = Bitmap.createBitmap(
                horizontalBitmap,
                0,
                0,
                horizontalBitmap.width,
                horizontalBitmap.height,
                matrix,
                false
            )

            result = decoding(verticalBitmap)
        }

        /*
         * Response
         */

        return result
    }

    /**
     * Convert bitmap to IntArray
     */
    private fun bitmapToIntArray(bitmap: Bitmap): IntArray {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(
            intArray,
            0,
            bitmap.width,
            0,
            0,
            bitmap.width,
            bitmap.height
        )
        return intArray
    }

    /**
     * Decoding bitmap
     */
    private fun decoding(bitmap: Bitmap): com.google.zxing.Result? {
        return try {
            decoder.decodeWithState(
                BinaryBitmap(
                    HybridBinarizer(
                        RGBLuminanceSource(
                            bitmap.width,
                            bitmap.height,
                            bitmapToIntArray(bitmap)
                        )
                    )
                )
            )
        } catch (e: Exception) {
            null
        }
    }
}