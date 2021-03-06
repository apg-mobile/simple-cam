package com.github.siwarats.simplecam.app

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class BarcodeStorageController {

    companion object {
        private const val TAG = "Image"
        private const val JPEG_EXTENSION = ".jpg"
        private const val GET_IMAGE_FOLDER = "/images/barcode_capture"
    }

    fun getImages(context: Context): MutableList<File> {
        val dir = getImageDir(context)

        val jpgFiles = dir.listFiles { _, name ->
            name?.contains(JPEG_EXTENSION) == true
        }

        return jpgFiles?.toMutableList() ?: mutableListOf()
    }

    fun saveImage(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        val dir = getImageDir(context)

        if (!dir.exists()) {
            dir.mkdirs()
            Log.e(TAG, "saveImage() => Create folder, ${dir.path}")
        }

        val file = File(dir, "$fileName$JPEG_EXTENSION")

        if (file.exists()) {
            file.delete()
            Log.e(TAG, "saveImage() => Duplicate file was deleted")
        }

        return try {
            if (file.createNewFile()) {
                Log.e(TAG, "saveImage() => Create new file, ${file.path}")
                return try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
                    Log.e(TAG, "saveImage() => Success")
                    true
                } catch (e: Exception) {
                    Log.e(TAG, "saveImage() => Error, ${e.message}")
                    false
                }
            } else {
                Log.e(TAG, "saveImage() => Can not create new file, ${file.path}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "saveImage() => Error, ${e.message}")
            false
        }
    }

    private fun getImageDir(context: Context) = File(ContextCompat.getDataDir(context), GET_IMAGE_FOLDER)
}