package com.hive

import com.exela.teacher.ApiRequest
import com.exela.teacher.NetworkRequest
import com.hive.exts.requestToFlow
import com.hive.model.BaseApiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import sa.zad.easyretrofit.observables.NeverErrorObservable

class VmFrameworkNetworkRequest : NetworkRequest {

    private val _apiRequest = MutableStateFlow<ApiRequest<Any?>?>(null)

    override val apiRequest = _apiRequest.filterNotNull()

    override fun <M, E : BaseApiError> toFlowDataState(
        request: NeverErrorObservable<M>,
        requestId: Int,
        eClass: Class<E>
    ): Flow<DataState<M>> {
        return request.requestToFlow(requestId, eClass)
            .onEach {
                _apiRequest.value = ApiRequest(it)
            }
    }

}