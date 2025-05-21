package com.hive.exts

import android.content.Context
import android.content.Intent
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AppUtilsTest {

    @Captor
    lateinit var intentCaptor: ArgumentCaptor<Intent>

    @Test
    fun testOpenAppSettings() {
        val mockContext = Mockito.mock(Context::class.java)
        val packageName = "com.exela.teacher"

        // Mock the context's package name
        Mockito.`when`(mockContext.packageName).thenReturn(packageName)

        openAppSettings(mockContext)

        // Capture the intent passed to startActivity
        intentCaptor = ArgumentCaptor.forClass(Intent::class.java)
        verify(mockContext).startActivity(intentCaptor.capture())

        val capturedIntent = intentCaptor.value
        assertEquals(ACTION_APPLICATION_DETAILS_SETTINGS, capturedIntent.action)
        assertEquals("package:$packageName", capturedIntent.data.toString())
    }
}

