package com.hive.exts

import android.net.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class StringExtTest {

    @Test
    fun test_name_extension_with_firstname_true() {
        // Single name
        assertEquals("John", "John".name())

        // Two names
        assertEquals("John", "John Smith".name())

        // Multiple names
        assertEquals("John", "John James Smith".name())

        // Name with extra spaces
        assertEquals("John", "  John   Smith  ".name())
    }

    @Test
    fun test_name_extension_with_firstname_false() {
        // Single name
        assertEquals("", "John".name(firstName = false))

        // Two names
        assertEquals("Smith", "John Smith".name(firstName = false))

        // Multiple names
        assertEquals("James Smith", "John James Smith".name(firstName = false))

        // Name with extra spaces
        assertEquals("Smith", "  John   Smith  ".name(firstName = false))
    }

    @Test
    fun test_striptrailingleadingnewlines_extension() {
        // Leading newlines
        assertEquals("test", "\ntest".stripTrailingLeadingNewLines)
        assertEquals("test", "\r\ntest".stripTrailingLeadingNewLines)

        // Trailing newlines
        assertEquals("test", "test\n".stripTrailingLeadingNewLines)
        assertEquals("test", "test\r\n".stripTrailingLeadingNewLines)

        // Both leading and trailing newlines
        assertEquals("test", "\ntest\n".stripTrailingLeadingNewLines)
        assertEquals("test", "\r\ntest\r\n".stripTrailingLeadingNewLines)

        // Multiple newlines
        assertEquals("test", "\n\n\ntest\n\n".stripTrailingLeadingNewLines)

        // No newlines
        assertEquals("test", "test".stripTrailingLeadingNewLines)
    }

    @Test
    fun test_emptyornull_extension() {
        // Null string
        val nullString: String? = null
        assertEquals("default", nullString emptyOrNull "default")

        // Empty string
        assertEquals("default", "" emptyOrNull "default")

        // Blank string
        assertEquals("   ", "   " emptyOrNull "default")

        // Normal string
        assertEquals("test", "test" emptyOrNull "default")
    }

    @Test
    fun test_touri_extension() {
        // Valid URL
        assertEquals(
            Uri.parse("https://example.com"),
            "https://example.com".toUri
        )

        // URL with query parameters
        assertEquals(
            Uri.parse("https://example.com?param=value"),
            "https://example.com?param=value".toUri
        )

        // Null string
        val nullString: String? = null
        assertNull(nullString.toUri)

        // Invalid URL (Uri.parse handles this gracefully)
        assertEquals(
            Uri.parse("not a url"),
            "not a url".toUri
        )
    }
}