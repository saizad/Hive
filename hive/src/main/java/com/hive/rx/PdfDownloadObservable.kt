package com.hive.rx

import io.reactivex.Observable
import retrofit2.Response
import sa.zad.easyretrofit.ProgressListener.Progress
import sa.zad.easyretrofit.observables.FileDownloadObservable
import java.io.File

class PdfDownloadObservable(upstream: Observable<Response<Progress<File>>>) :
    FileDownloadObservable(upstream)