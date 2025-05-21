package com.hive.exts

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import java.io.ByteArrayInputStream
import java.io.File

//@RunWith(RobolectricTestRunner::class)
class FileExtensionsTest {

    private lateinit var tempFile: File
    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver

    @Before
    fun setup() {
        tempFile = File.createTempFile("test", ".tmp")
        tempFile.writeText("Hello, World!")
        context = mock(Context::class.java)
        contentResolver = mock(ContentResolver::class.java)
        `when`(context.contentResolver).thenReturn(contentResolver)
        `when`(context.cacheDir).thenReturn(File("/tmp"))
    }

    @After
    fun tearDown() {
        tempFile.delete()
        unmockkAll()
    }

    @Test
    fun file_size_should_return_correct_byte_size() {
        assertEquals(tempFile.length().toDouble(), tempFile.size, 0.0)
    }

    @Test
    fun file_size_in_kb_should_be_calculated_correctly() {
        assertEquals(tempFile.size / 1024, tempFile.sizeInKb, 0.0)
    }

    @Test
    fun file_size_in_mb_should_be_calculated_correctly() {
        assertEquals(tempFile.sizeInKb / 1024, tempFile.sizeInMb, 0.0)
    }

    @Test
    fun sizestr_should_return_correct_byte_size_as_string() {
        assertEquals(tempFile.size.toString(), tempFile.sizeStr())
    }

    @Test
    fun sizestrwithbytes_should_append__b_() {
        assertEquals("${tempFile.size}b", tempFile.sizeStrWithBytes())
    }

    @Test
    fun sizestrwithkb_should_append__kb_() {
        assertEquals("${"%.0f".format(tempFile.sizeInKb)}Kb", tempFile.sizeStrWithKb(0))
    }

    @Test
    fun sizestrwithmb_should_append__mb_() {
        assertEquals("${"%.0f".format(tempFile.sizeInMb)}Mb", tempFile.sizeStrWithMb(0))
    }

    @Test
    fun uri_tofileornull_should_return_null_if_conversion_fails() {
        val mockUri = mockk<Uri>()

        every { mockUri.scheme } returns "content"
        every { mockUri.path } returns "/mock/path/to/file.jpg"

        val result = mockUri.toFileOrNull

        assertNull(result)
    }


    @Test
    fun uri_tofileornull_should_return_file_if_conversion_succeeds() {
        val mockUri = mockk<Uri>()
        val mockFilePath = "/mock/path/to/file.jpg"
        val mockFile = File(mockFilePath)

        
        every { mockUri.scheme } returns "file"
        every { mockUri.path } returns mockFilePath

        val result = mockUri.toFileOrNull

        assertNotNull(result)
        assertEquals(mockFile.path, result?.path)
    }

    @Test
    fun test_getfilename_with_content_scheme() {
        val uri = mock(Uri::class.java)
        `when`(uri.scheme).thenReturn("content")

        val cursor = mock(Cursor::class.java)
        `when`(cursor.moveToFirst()).thenReturn(true)
        `when`(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).thenReturn(0)
        `when`(cursor.getString(0)).thenReturn("test_file.txt")

        `when`(contentResolver.query(eq(uri), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(cursor)

        val fileName = getFileName(uri, context)
        assertEquals("test_file.txt", fileName)

        verify(cursor).close()
    }


    @Test
    fun test_getfilename_with_file_scheme() {
        val uri = mock(Uri::class.java)
        `when`(uri.scheme).thenReturn("file")
        `when`(uri.path).thenReturn("/some/path/to/test_file.txt")

        val fileName = getFileName(uri, context)
        assertEquals("test_file.txt", fileName)
    }

    @Test
    fun test_getfilename_with_null_filename_fallback() {
        val uri = mock(Uri::class.java)
        `when`(uri.scheme).thenReturn("file")
        `when`(uri.path).thenReturn(null)

        val fileName = getFileName(uri, context)
        assertEquals("unknown_file", fileName)
    }

    @Test
    fun test_extractfile() {
        val uri = mock(Uri::class.java)
        val inputStream = ByteArrayInputStream("test content".toByteArray())
        `when`(uri.scheme).thenReturn("content")
        `when`(uri.path).thenReturn("/some/path/to/test_file.txt")

        val cursor = mock(Cursor::class.java)
        `when`(cursor.moveToFirst()).thenReturn(true)
        `when`(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).thenReturn(0)
        `when`(cursor.getString(0)).thenReturn("test_file.txt")

        `when`(contentResolver.query(eq(uri), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(cursor)
        `when`(contentResolver.openInputStream(uri)).thenReturn(inputStream)

        val resultFile = extractFile(uri, context)

        assertEquals(File(context.cacheDir, "test_file.txt"), resultFile)
        assertEquals("test content", resultFile.readText())

        verify(cursor).close()
    }

}
