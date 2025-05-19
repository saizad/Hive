package com.hive.rx

import android.app.Application
import com.google.gson.Gson
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sa.zad.easyretrofit.EasyRetrofit
import sa.zad.easyretrofit.EasyRetrofitClient
import sa.zad.easyretrofit.base.BaseEasyRetrofitCallAdapterFactory
import java.io.File

open class VmFrameworkEasyRetrofit(
    val application: Application,
    private val gson: Gson,
    private val domainUrl: String,
    private val isDebugMode: Boolean = false
) : EasyRetrofit(application) {

    val client = VmFrameworkEasyRetrofitClient(application, isDebugMode)

    override fun retrofitBuilderReady(retrofitBuilder: Retrofit.Builder): Retrofit.Builder {
        return retrofitBuilder
            .baseUrl(domainUrl)
    }

    override fun addConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    override fun easyRetrofitClient(): EasyRetrofitClient {
        return client
    }

    override fun addCallAdapterFactory(): BaseEasyRetrofitCallAdapterFactory {
        return VmFrameworkEasyRetrofitCallAdapterFactory(
            File(application.cacheDir, "${(1000..1000000).random()}_download.pdf")
        )
    }

}