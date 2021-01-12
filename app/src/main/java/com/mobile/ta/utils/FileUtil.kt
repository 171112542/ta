package com.mobile.ta.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.File

object FileUtil {

    fun getFilePath(fileUri: Uri, contentResolver: ContentResolver): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        val cursor = contentResolver.query(fileUri, filePathColumn, null, null, null)
        cursor?.moveToFirst()

        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = columnIndex?.let {
            cursor.getString(it)
        }
        cursor?.close()

        return filePath
    }

    fun convertFilePathToBitmap(filePath: String): Bitmap? = if (File(filePath).exists()) {
        BitmapFactory.decodeFile(filePath)
    } else {
        null
    }
}