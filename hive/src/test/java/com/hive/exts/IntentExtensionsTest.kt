package com.hive.exts

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.ByteArrayInputStream
import java.io.File

private class TestActivity : Activity()

@RunWith(AndroidJUnit4::class)
class IntentExtensionsTest {

    @Test
    fun test_componentintent_creates_intent_for_specific_class() {
        val mockContext = mock(Context::class.java)

        val intent = mockContext.componentIntent<TestActivity> {
            putExtra("key", "value")
        }

        assertNotNull(intent)
        assertEquals(TestActivity::class.java.name, intent.component?.className)
        assertEquals("value", intent.getStringExtra("key"))
    }

    @Test
    fun test_imagepickerintent_configurations() {
        val intent = imagePickerIntent()

        assertEquals(Intent.ACTION_PICK, intent.action)
        assertEquals("image/*", intent.type)
        assertEquals("true", intent.getStringExtra("crop"))
        assertEquals(true, intent.getBooleanExtra("scale", false))
        assertEquals(256, intent.getIntExtra("outputX", 0))
        assertEquals(256, intent.getIntExtra("outputY", 0))
        assertEquals(1, intent.getIntExtra("aspectX", 0))
        assertEquals(1, intent.getIntExtra("aspectY", 0))
    }

    @Test
    fun test_selectedfile_extracts_file_paths_from_single_uri() {
        val mockContext = mock(Context::class.java)
        val mockContentResolver = mock(ContentResolver::class.java)
        `when`(mockContext.contentResolver).thenReturn(mockContentResolver)

        // Simulating a Uri and file content
        val testUri = Uri.parse("content://test/path/to/image.jpg")
        val inputStream = ByteArrayInputStream("test file content".toByteArray())
        `when`(mockContentResolver.openInputStream(testUri)).thenReturn(inputStream)

        // Mock Intent
        val intent = mock(Intent::class.java)
        `when`(intent.data).thenReturn(testUri)

        // Run the function
        val files = intent.selectedFile()

        // Validate the results
        assertEquals(1, files.size)
        assertTrue("Expected file path to end with the file name", files[0].endsWith("image.jpg"))
    }


    @Test
    fun test_selectedfile_extracts_file_paths_from_multiple_uris() {
        val mockContext = mock(Context::class.java)
        val mockContentResolver = mock(ContentResolver::class.java)
        `when`(mockContext.contentResolver).thenReturn(mockContentResolver)

        // Simulating two Uris and their content
        val testUri1 = Uri.parse("content://test/path/to/image1.jpg")
        val testUri2 = Uri.parse("content://test/path/to/image2.jpg")
        val inputStream1 = ByteArrayInputStream("file1 content".toByteArray())
        val inputStream2 = ByteArrayInputStream("file2 content".toByteArray())
        `when`(mockContentResolver.openInputStream(testUri1)).thenReturn(inputStream1)
        `when`(mockContentResolver.openInputStream(testUri2)).thenReturn(inputStream2)

        // Mocking ClipData with two items
        val clipData = mock(android.content.ClipData::class.java)
        val item1 = mock(android.content.ClipData.Item::class.java)
        val item2 = mock(android.content.ClipData.Item::class.java)
        `when`(item1.uri).thenReturn(testUri1)
        `when`(item2.uri).thenReturn(testUri2)
        `when`(clipData.getItemAt(0)).thenReturn(item1)
        `when`(clipData.getItemAt(1)).thenReturn(item2)
        `when`(clipData.itemCount).thenReturn(2)

        // Mocking Intent with ClipData
        val intent = mock(Intent::class.java)
        `when`(intent.clipData).thenReturn(clipData)

        // Run the function
        val files = intent.selectedFile()

        // Validate results
        assertEquals(2, files.size)
        assertTrue("Expected first file path to end with image1.jpg", files[0].endsWith("image1.jpg"))
        assertTrue("Expected second file path to end with image2.jpg", files[1].endsWith("image2.jpg"))
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
        
        assertFalse("Old file should be deleted", oldFile.exists())
        assertTrue("Recent file should still exist", newFile.exists())
        
        // Cleanup
        mockCacheDir.deleteRecursively()
    }

}
