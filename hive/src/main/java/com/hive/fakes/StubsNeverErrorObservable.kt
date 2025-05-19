package com.hive.fakes

import com.hive.exts.alsoLog
import io.reactivex.Observable
import retrofit2.Response
import sa.zad.easyretrofit.observables.NeverErrorObservable
import java.util.concurrent.TimeUnit

internal class StubsNeverErrorObservable<T>(
    upstream: Observable<Response<T>>,
    delayInMillis: Long = (10..2000).random().toLong()
) : NeverErrorObservable<T>(upstream) {

    init {
        this.upstream = upstream.delay(delayInMillis, TimeUnit.MILLISECONDS)
            .doOnNext {
                it.body().alsoLog("StubsLog")
                it.errorBody().alsoLog("StubsLog-error")
                it
            }
    }
}