package com.hive.rx

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test

class TextToJsonInterceptorTest {

    private val interceptor = TextToJsonInterceptor()

    @Test
    fun test_content_type_is_changed_from_text_plain_to_application_json() {
        val server = MockWebServer()
        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "text/plain")
                .setBody("Plain text response")
        )

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val request = Request.Builder()
            .url(server.url("/"))
            .build()

        val response = client.newCall(request).execute()
        assertEquals("application/json; charset=utf-8", response.header("Content-Type"))
    }

    @Test
    fun test_content_type_is_not_changed_when_it_s_not_text_plain() {
        val server = MockWebServer()
        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("JSON response")
        )

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val request = Request.Builder()
            .url(server.url("/"))
            .build()

        val response = client.newCall(request).execute()
        assertEquals("application/json", response.header("Content-Type"))
    }

    @Test
    fun test_no_content_type_header_is_handled_correctly() {
        val server = MockWebServer()
        server.enqueue(
            MockResponse()
                .setBody("No Content-Type response")
        )

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val request = Request.Builder()
            .url(server.url("/"))
            .build()

        val response = client.newCall(request).execute()
        // Assert that no Content-Type header exists
        assertEquals(null, response.header("Content-Type"))
    }
}
