package com.hive.rx

import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import rx.functions.Action1
import sa.zad.easyretrofit.ProgressListener.Progress
import sa.zad.easyretrofit.Utils
import sa.zad.easyretrofit.call.CallFileDownloadEnqueue
import java.io.File

class PdfDownloadEnqueue(originalCall: Call<Progress<File>>, private val file: File) :
    CallFileDownloadEnqueue(originalCall) {

    @Throws(Exception::class)
    override fun responseBodyReady(
        responseBody: ResponseBody, url: HttpUrl, writtenCallback: Action1<Long>
    ): File {
        val saveTo = saveTo(url)
        Utils.writeStreamToFile(responseBody.byteStream(), saveTo, writtenCallback)
        return saveTo
    }

    @Throws(Exception::class)
    override fun saveTo(url: HttpUrl): File {
        return File(file.parentFile!!.absolutePath, "${(1000..1000000).random()}_download.pdf")
    }
}