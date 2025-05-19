package com.hive.rx

import retrofit2.CallAdapter
import sa.zad.easyretrofit.base.EasyRetrofitCallAdapterFactory
import java.io.File
import java.lang.reflect.Type

class VmFrameworkEasyRetrofitCallAdapterFactory(private val file: File) : EasyRetrofitCallAdapterFactory() {

    override fun getCallAdapter(
        rawType: Class<*>, responseType: Type?, parameterizedType: Class<*>?
    ): CallAdapter<*, *>? {

        return if (rawType == PdfDownloadObservable::class.java) {
            PdfDownloadCallAdapter(file)
        } else {
            super.getCallAdapter(rawType, responseType, parameterizedType)
        }
    }
}