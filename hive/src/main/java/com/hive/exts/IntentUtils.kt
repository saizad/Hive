package com.hive.exts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Creates an intent for a specific component (class) with optional configurations.
 */
inline fun <reified T> Context.componentIntent(config: Intent.() -> Unit = {}): Intent {
    return Intent(this, T::class.java).apply(config)
}

/**
 * Creates an intent to pick an image from the gallery with cropping and scaling configurationsxx.
 */
fun imagePickerIntent(): Intent {
    return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
        type = "image/*"
        putExtra("crop", "true") // Note: May not be supported on all devices
        putExtra("scale", true)
        putExtra("outputX", 256)
        putExtra("outputY", 256)
        putExtra("aspectX", 1)
        putExtra("aspectY", 1)
        putExtra("return-data", true) // Renamed to a more common flag name
    }
}

/**
 * Extracts file paths from the Intent's selected images (handles single and multiple selections).
 * Handles Scoped Storage for Android 10+.
 */
fun Intent.selectedFile(context: Context): List<String> {
    val filePaths = mutableListOf<String>()
    val clipData = this.clipData

    if (clipData != null) {
        for (i in 0 until clipData.itemCount) {
            val uri = clipData.getItemAt(i).uri
            uri?.let {
                val path = getFilePathFromUri(it, context)
                filePaths.add(path)
            }
        }
    } else {
        this.data?.let {
            val path = getFilePathFromUri(it, context)
            filePaths.add(path)
        }
    }

    return filePaths
}


/**
 * Converts a Uri into a file path or copies the file to the app's cache directory
 * if direct access is not possible (Scoped Storage compliance).
 * 
 */
fun getFilePathFromUri(uri: Uri, context: Context): String {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Failed to open input stream")

    val fileName = getFileName(uri, context)
    val tempFile = File(context.cacheDir, "temp_$fileName")

    inputStream.use { stream ->
        FileOutputStream(tempFile).use { output ->
            stream.copyTo(output)
        }
    }

    return tempFile.absolutePath
}

fun getCipher(mode: Int, iv: ByteArray? = null): Cipher {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)
    
    if (!keyStore.containsAlias(KEY_ALIAS)) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)  // Require user authentication
            .apply {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    setUserAuthenticationParameters(30, KeyProperties.AUTH_BIOMETRIC_STRONG)
                } else {
                    @Suppress("DEPRECATION")
                    setUserAuthenticationValidityDurationSeconds(30)
                }
            }
            .setRandomizedEncryptionRequired(true)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
    return Cipher.getInstance("AES/GCM/NoPadding").apply {
        if (iv != null) {
            init(mode, key, GCMParameterSpec(128, iv))
        } else {
            init(mode, key)
        }
    }
}

const val KEY_ALIAS = "FileEncryptionKey"

fun cleanupOldCacheFiles(cacheDir: File) {
    val maxAge = 24 * 60 * 60 * 1000
    val currentTime = System.currentTimeMillis()
    
    cacheDir.listFiles()?.forEach { file ->
        if (file.name.startsWith("temp_") && currentTime - file.lastModified() > maxAge) {
            file.deleteIfExists()
        }
    }
}
