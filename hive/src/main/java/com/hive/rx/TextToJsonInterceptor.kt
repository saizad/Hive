package com.hive.rx

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class TextToJsonInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())

        val intercept = originalResponse.header("Content-Type", "")!!.contains("text/plain")
        if (intercept) {
            return originalResponse.newBuilder()
                .header("Content-Type", "application/json; charset=utf-8")
                .build()
        }

        return originalResponse
    }
}
