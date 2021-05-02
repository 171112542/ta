package com.mobile.ta.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.File

object FileUtil {

    fun getFileAbsolutePath(contentResolver: ContentResolver, uri: Uri): String? {
        var result: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        if (cursor?.moveToFirst().orFalse()) {
            val columnIndex = cursor?.getColumnIndex(projection[0])
            columnIndex?.let {
                result = cursor.getString(it)
            }
        }
        cursor?.close()

        return result
    }

    fun convertFilePathToBitmap(filePath: String): Bitmap? = if (File(filePath).exists()) {
        BitmapFactory.decodeFile(filePath)
    } else {
        null
    }
}