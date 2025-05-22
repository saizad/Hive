package com.hive.exts

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import javax.crypto.Cipher

@RunWith(AndroidJUnit4::class)
class IntentUtilsTest {
    private lateinit var context: Context
    private lateinit var testFile: File

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        testFile = File(context.cacheDir, "test_image.jpg")
        FileOutputStream(testFile).use { output ->
            output.write("test content".toByteArray())
        }
    }

    @Test
    fun getFilePathFromUri_validUri_returnsValidPath() {

        val uri = Uri.fromFile(testFile)


        val resultPath = getFilePathFromUri(uri, context)

        assertNotNull(resultPath)
        assertTrue(File(resultPath).exists())
        assertTrue(resultPath.contains("temp_"))
    }

    @Test(expected = FileNotFoundException::class)
    fun getFilePathFromUri_invalidUri_throwsException() {
        
        val invalidUri = Uri.parse("content://invalid/path")

        getFilePathFromUri(invalidUri, context)
    }

    @Test
    fun getCipher_keyGeneration_throwsWhenNoBiometricsEnrolled() {
        
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        assertThrows(InvalidAlgorithmParameterException::class.java) {
            getCipher(Cipher.ENCRYPT_MODE)
        }
    }

    @After
    fun cleanup() {
        testFile.delete()
        context.cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("temp_")) {
                file.delete()
            }
        }
    }
} 