package com.hive.exts

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream


fun Bitmap.save(file: File, compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG) {
    if (file.exists()) {
        file.delete()
    }
    try {
        val out = FileOutputStream(file)
        compress(compressFormat, 90, out)
        out.flush()
        out.close()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}
