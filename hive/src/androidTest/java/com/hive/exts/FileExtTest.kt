package com.hive.exts

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class FileExtensionsTest {
    private lateinit var context: Context
    private lateinit var testImageFile: File
    private lateinit var testImageUri: Uri

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        
        // Create a test image
        testImageFile = File(context.cacheDir, "test_image.jpg")
        createTestImage(testImageFile)
        
        // Get URI for the test image
        testImageUri = Uri.fromFile(testImageFile)
    }

    @After
    fun tearDown() {
        testImageFile.delete()
    }

    @Test
    fun uri_imageuritofile_should_create_a_file() {
        // Execute
        val resultFile = testImageUri.imageUriToFile(context)

        // Verify
        assertTrue(resultFile.exists())
        assertEquals("test_image.jpg", resultFile.name)
        assertTrue(resultFile.length() > 0)
        
        // Clean up
        resultFile.delete()
    }

    private fun createTestImage(file: File) {
        // Create a simple bitmap
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        
        // Save it to the file
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        
        // Clean up bitmap
        bitmap.recycle()
    }
} 