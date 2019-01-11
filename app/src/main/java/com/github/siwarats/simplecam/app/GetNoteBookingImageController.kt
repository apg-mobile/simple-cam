package com.github.siwarats.simplecam.app

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class GetNoteBookingImageController {

    companion object {
        private const val TAG = "BookingImage"
        private const val JPEG_EXTENSION = ".jpg"
        private const val GET_IMAGE_FOLDER = "/images/getnote"
    }

    /**
     * Get booking image files in JPEG.
     * @return list of File
     */
    fun getBookingImages(context: Context): MutableList<File> {
        val dir = getImageDir(context)

        val jpgFiles = dir.listFiles { _, name ->
            name?.contains(JPEG_EXTENSION) == true
        }

        return jpgFiles?.toMutableList() ?: mutableListOf()
    }

    /**
     * Save booking image file.
     * @return true if success
     */
    fun saveBookingImage(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        val dir = getImageDir(context)

        if (!dir.exists()) {
            dir.mkdirs()
            Log.e(TAG, "saveBookingImage() => Create folder, ${dir.path}")
        }

        val file = File(dir, "$fileName$JPEG_EXTENSION")

        if (file.exists()) {
            file.delete()
            Log.e(TAG, "saveBookingImage() => Duplicate file was deleted")
        }

        return try {
            if (file.createNewFile()) {
                Log.e(TAG, "saveBookingImage() => Create new file, ${file.path}")
                return try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
                    Log.e(TAG, "saveBookingImage() => Success")
                    true
                } catch (e: Exception) {
                    Log.e(TAG, "saveBookingImage() => Error, ${e.message}")
                    false
                }
            } else {
                Log.e(TAG, "saveBookingImage() => Can not create new file, ${file.path}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "saveBookingImage() => Error, ${e.message}")
            false
        }
    }

    private fun getImageDir(context: Context) = File(ContextCompat.getDataDir(context), GET_IMAGE_FOLDER)
}