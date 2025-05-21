package com.hive

import com.hive.model.BaseApiError
import com.hive.model.ErrorModel
import kotlinx.coroutines.flow.Flow
import sa.zad.easyretrofit.observables.NeverErrorObservable

interface NetworkRequest {

    val apiRequest: Flow<ApiRequest<*>>

    fun <M> toFlowDataState(
        request: NeverErrorObservable<M>,
        requestId: Int,
    ): Flow<DataState<M>> {
        return toFlowDataState(request, requestId, ErrorModel::class.java)
    }

    fun <M, E : BaseApiError> toFlowDataState(
        request: NeverErrorObservable<M>,
        requestId: Int, eClass: Class<E>
    ): Flow<DataState<M>>
}