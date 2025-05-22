package com.hive.exts

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

private class TestActivity : Activity()

@RunWith(AndroidJUnit4::class)
class IntentExtensionsTest {

    @Test
    fun test_componentintent_creates_intent_for_specific_class() {
        val mockContext = ApplicationProvider.getApplicationContext<Context>()

        val intent = mockContext.componentIntent<TestActivity> {
            putExtra("key", "value")
        }

        Assert.assertNotNull(intent)
        Assert.assertEquals(
            TestActivity::class.java.name,
            intent.component?.className
        )
        Assert.assertEquals("value", intent.getStringExtra("key"))
    }

    @Test
    fun test_imagepickerintent_configurations() {
        val intent = imagePickerIntent()

        Assert.assertEquals(Intent.ACTION_PICK, intent.action)
        Assert.assertEquals("image/*", intent.type)
        Assert.assertEquals("true", intent.getStringExtra("crop"))
        Assert.assertEquals(true, intent.getBooleanExtra("scale", false))
        Assert.assertEquals(256, intent.getIntExtra("outputX", 0))
        Assert.assertEquals(256, intent.getIntExtra("outputY", 0))
        Assert.assertEquals(1, intent.getIntExtra("aspectX", 0))
        Assert.assertEquals(1, intent.getIntExtra("aspectY", 0))
    }

    @Test
    fun test_selectedfile_extracts_file_paths_from_single_uri() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Create a test file in the cache directory
        val fileName = "test_image.jpg"
        val testFile = File(context.cacheDir, fileName)
        testFile.writeText("dummy content")

        val testUri = FileProvider.getUriForFile(context, "com.hive.fileprovider", testFile)

        // Create the Intent and set data
        val intent = Intent().apply {
            data = testUri
        }

        // Run your real method
        val files = intent.selectedFile(context)

        // Assertions
        Assert.assertEquals(1, files.size)
        Assert.assertTrue(files[0].endsWith(fileName))
    }



    @Test
    fun test_selectedfile_extracts_file_paths_from_multiple_uris_real() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Create two temporary test files
        val file1 = File(context.cacheDir, "image1.jpg").apply {
            writeText("file 1 content")
        }
        val file2 = File(context.cacheDir, "image2.jpg").apply {
            writeText("file 2 content")
        }

        // Get URIs via FileProvider
        val uri1 = FileProvider.getUriForFile(context, "com.hive.fileprovider", file1)
        val uri2 = FileProvider.getUriForFile(context, "com.hive.fileprovider", file2)

        // Create ClipData manually
        val clipData = ClipData.newUri(context.contentResolver, "File1", uri1)
        clipData.addItem(ClipData.Item(uri2))

        // Create real Intent with ClipData
        val intent = Intent().apply {
            this.clipData = clipData
        }

        val files = intent.selectedFile(context)

        Assert.assertEquals(2, files.size)
        Assert.assertTrue(files[0].endsWith("image1.jpg"))
        Assert.assertTrue(files[1].endsWith("image2.jpg"))
    }

    @Test
    fun test_cleanupOldCacheFiles_removes_expired_files() {
        val mockCacheDir = createTempDir()

        // Create some old test files with a timestamp from 25 hours ago
        val oldTimestamp = System.currentTimeMillis() - (25 * 60 * 60 * 1000)
        val oldFile = File(mockCacheDir, "temp_${oldTimestamp}_test.txt")
        oldFile.createNewFile()
        // Set last modified time to match the old timestamp
        oldFile.setLastModified(oldTimestamp)

        // Create some recent test files
        val newTimestamp = System.currentTimeMillis()
        val newFile = File(mockCacheDir, "temp_${newTimestamp}_test.txt")
        newFile.createNewFile()
        newFile.setLastModified(newTimestamp)

        cleanupOldCacheFiles(mockCacheDir)

        Assert.assertFalse("Old file should be deleted", oldFile.exists())
        Assert.assertTrue("Recent file should still exist", newFile.exists())

        // Cleanup
        mockCacheDir.deleteRecursively()
    }

}