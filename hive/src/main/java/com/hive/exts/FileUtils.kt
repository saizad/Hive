package com.hive.exts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.pdf.PdfRenderer
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.graphics.decodeBitmap
import androidx.core.net.toFile
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024

fun File.sizeStr(): String = size.toString()
fun File.sizeStrInKb(decimals: Int = 0): String = "%.${decimals}f".format(sizeInKb)
fun File.sizeStrInMb(decimals: Int = 0): String = "%.${decimals}f".format(sizeInMb)

fun File.sizeStrWithBytes(): String = sizeStr() + "b"
fun File.sizeStrWithKb(decimals: Int = 0): String = sizeStrInKb(decimals) + "Kb"
fun File.sizeStrWithMb(decimals: Int = 0): String = sizeStrInMb(decimals) + "Mb"

fun Uri.imageUriToFile(context: Context, parent: File = context.cacheDir): File {
    val fileName = getFileName(this, context)
    val file = File(parent, fileName)
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source)
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }
    bitmap.save(file)
    return file
}

val Uri.toFileOrNull: File?
    get() {
        return try {
            toFile()
        } catch (e: IllegalArgumentException) {
            null
        }
    }

fun extractFile(uri: Uri, context: Context): File {
    val fileName = getFileName(uri, context)
    val file = File(context.cacheDir, fileName)

    sa.zad.easyretrofit.Utils.writeStreamToFile(
        context.contentResolver.openInputStream(uri)!!,
        file
    )
    return file
}

@SuppressLint("Range")
fun getFileName(uri: Uri, context: Context): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor.use {
            if (it != null && it.moveToFirst()) {
                result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = uri.path
        result?.let {
            val cut = it.lastIndexOf('/')
            if (cut != -1) {
                result = it.substring(cut + 1)
            }
        }
    }
    return result ?: "unknown_file"
}

/**
 * Safely writes data to a file using FileOutputStream or EncryptedFile.
 * Uses Android's EncryptedFile API when encryption is required.
 *
 * @param context Android context needed for encryption
 * @param writeOperation Lambda that performs the actual write operation
 */
fun File.writeBytes(
    context: Context,
    writeOperation: (FileOutputStream) -> Unit,
) {
    deleteIfExists()

    try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedFile = EncryptedFile.Builder(
            context,
            this,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        // Copy from temp file to encrypted file
        encryptedFile.openFileOutput().use { encryptedOutput ->
            writeOperation(encryptedOutput)
        }
    } catch (e: Exception) {
        println("Error writing file: ${e.message}")
        e.printStackTrace()
        throw e
    }
}

/**
 * Safely deletes a file if it exists.
 * @throws IOException if the file exists but cannot be deleted
 */
fun File.deleteIfExists() {
    if (exists() && !delete()) {
        throw IOException("Failed to delete existing file at $absolutePath")
    }
}

fun getPdfPageCount(context: Context, uri: Uri): Int {
    val contentResolver = context.contentResolver
    val fileDescriptor = contentResolver.openFileDescriptor(uri, "r") ?: return 0

    return fileDescriptor.use {
        val pdfRenderer = PdfRenderer(it)
        val pageCount = pdfRenderer.pageCount
        pdfRenderer.close()
        pageCount
    }
}

fun generatePdfThumbnailUri(context: Context, uri: Uri, pageIndex: Int = 0): Uri? {
    val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
    val pdfRenderer = PdfRenderer(fileDescriptor)

    if (pageIndex >= pdfRenderer.pageCount) {
        pdfRenderer.close()
        return null
    }

    val page = pdfRenderer.openPage(pageIndex)

    val width = page.width / 2
    val height = page.height / 2
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

    page.close()
    pdfRenderer.close()

    val fileName = "pdf_thumb_${System.currentTimeMillis()}.png"
    val tempFile = File(context.cacheDir, fileName)

    FileOutputStream(tempFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        tempFile
    )
}

fun Uri.thumbnail(context: Context): Uri? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, this)
        val bitmap = retriever.getFrameAtTime(1_000_000)
        retriever.release()
        if (bitmap == null) return null
        val fileName = "pdf_thumb_${System.currentTimeMillis()}.png"
        val tempFile = File(context.cacheDir, fileName)

        FileOutputStream(tempFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Uri.duration(context: Context): Long {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, this)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        durationStr?.toLongOrNull() ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        0L
    }
}

