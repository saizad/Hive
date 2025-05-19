package com.hive.rx

import android.app.Application
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import sa.zad.easyretrofit.CachePolicy
import sa.zad.easyretrofit.EasyRetrofitClient
import java.net.URLDecoder

open class VmFrameworkEasyRetrofitClient(
    val application: Application,
    val isDebugMode: Boolean = false,
) : EasyRetrofitClient(application) {

    var token: String? = null

    override fun builderReady(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        builder.addInterceptor(getAuthInterceptor())
        builder.addNetworkInterceptor(StethoInterceptor())
        if (isDebugMode) {
            builder.addNetworkInterceptor(TextToJsonInterceptor())
        }
        return builder
    }

    override fun loggingLevel(): HttpLoggingInterceptor.Level {
        return if (isDebugMode) HttpLoggingInterceptor.Level.NONE else HttpLoggingInterceptor.Level.NONE
    }

    override fun cacheStale(cachePolicy: Int): Long {
        return if (cachePolicy == CachePolicy.LOCAL_IF_FRESH) {
            60L
        } else super.cacheStale(cachePolicy)
    }

    override fun connectTimeoutMilliseconds(): Long {
        return 0
    }

    override fun readTimeoutMilliseconds(): Long {
        return 0
    }

    override fun writeTimeoutMilliseconds(): Long {
        return 0
    }

    private fun getAuthInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            var request = chain.request()
            val decode = URLDecoder.decode(request.url.toString(), "UTF-8")
            request = request.newBuilder().url(decode).build()
            if (token != null && request.header("Authorization") == null) {
                request =
                    request.newBuilder().header("Authorization", "Bearer $token").build()
            }
            chain.proceed(request)
        }
    }

}