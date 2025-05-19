package com.hive.rx

import io.reactivex.Observable
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import sa.zad.easyretrofit.ProgressListener.Progress
import sa.zad.easyretrofit.call.adapter.FileDownloadCallAdapter
import sa.zad.easyretrofit.observables.FileDownloadObservable
import java.io.File

class PdfDownloadCallAdapter(private val file: File) : FileDownloadCallAdapter() {

    override fun call(call: Call<Progress<File>>): Observable<Response<Progress<File>>> {
        return PdfDownloadEnqueue(call, file)
    }

    override fun get(
        callMade: Observable<Response<Progress<File>>>,
        request: Request
    ): FileDownloadObservable {
        return PdfDownloadObservable(callMade)
    }
}