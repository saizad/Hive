package com.hive.exts

import com.hive.ApiErrorException
import com.hive.DataState
import com.hive.UiData
import com.hive.model.BaseApiError
import com.hive.model.DataModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import sa.zad.easyretrofit.observables.NeverErrorObservable

fun <T> Flow<DataState<T>>.filterLoading(): Flow<DataState.Loading> = filterIsInstance()

fun <T> Flow<DataState<T>>.filterSuccess(): Flow<DataState.Success<T>> = filterIsInstance()

fun <T> Flow<DataState<T>>.filterError(): Flow<DataState.Error> = filterIsInstance()

fun <T> Flow<DataState<T>>.filterApiError(): Flow<DataState.ApiError> = filterIsInstance()

fun <T> Flow<DataState<T>>.filterAnyError(): Flow<DataState.Error> {
    return filter {
        it is DataState.Error || it is DataState.ApiError
    }.mapNotNull { state ->
        when (state) {
            is DataState.ApiError -> DataState.Error(state.apiErrorException, state.requestId)
            is DataState.Error -> state
            else -> null
        }
    }
}

// Combine success data extraction into one function
fun <T> Flow<DataState<T>>.extractSuccessData(): Flow<T?> {
    return filterSuccess().map { it.data }
}

fun <T> Flow<DataState<DataModel<T>>>.extractDataModel(): Flow<DataModel<T>> {
    return filterSuccess().mapNotNull { it.data }
}

fun <T> Flow<DataState<DataModel<T>>>.extractDataFromDataModel(): Flow<T?> {
    return extractDataModel().map { it.data }
}

fun <T> Flow<DataState<DataModel<T>>>.requireExtractDataFromDataModel(): Flow<T> {
    return extractDataModel().mapNotNull {
        it.data ?: throw IllegalStateException("DataModel data is null")
    }
}

// Reuse for common error-handling flows
fun <T> Flow<DataState<T>>.onError(action: suspend (String) -> Unit): Flow<DataState<T>> {
    return onEach { state ->
        val message = when (state) {
            is DataState.Error -> state.throwable.message ?: "--NA--"
            is DataState.ApiError -> state.apiErrorException.errorModel.message()
            else -> null
        }
        message?.let { action(it) }
    }
}

fun <T> Flow<DataState<T>>.onSuccessState(callback: suspend (T?) -> Unit): Flow<DataState<T>> {
    return onEach {
        if (it is DataState.Success) callback(it.data)
    }
}

// Reduce redundancy in error callbacks
fun <T> Flow<DataState<T>>.onErrorState(callback: suspend (DataState.Error) -> Unit): Flow<DataState<T>> {
    return onEach {
        if (it is DataState.Error) callback(it)
    }
}

fun <T> Flow<DataState<T>>.onApiErrorState(callback: suspend (DataState.ApiError) -> Unit): Flow<DataState<T>> {
    return onEach {
        if (it is DataState.ApiError) callback(it)
    }
}

fun <T> Flow<DataState<T>>.onAnyErrorState(callback: suspend (DataState.Error) -> Unit): Flow<DataState<T>> {
    return onEach {
        when (it) {
            is DataState.ApiError -> callback(DataState.Error(it.apiErrorException, it.requestId))
            is DataState.Error -> callback(it)
            else -> Unit
        }
    }
}

// Consolidate mapping into one reusable function
inline fun <T, R> Flow<DataState<T>>.mapState(crossinline transform: suspend (T?) -> R): Flow<DataState<R>> {
    return map { state ->
        when (state) {
            is DataState.Success -> DataState.Success(transform(state.data), state.requestId)
            is DataState.Error -> state
            is DataState.ApiError -> state
            is DataState.Loading -> state
        }
    }
}

fun <T> dataStateApiErrorFlow(
    apiErrorException: ApiErrorException,
    requestId: Int
): Flow<DataState<T>> {
    return flow {
        emit(DataState.Loading(requestId))
        kotlinx.coroutines.delay(10)
        emit(DataState.ApiError(apiErrorException, requestId))
    }
}

fun <T> dataStateErrorFlow(emitValue: Throwable, requestId: Int): Flow<DataState<T>> {
    return flow {
        emit(DataState.Loading(requestId))
        kotlinx.coroutines.delay(10)
        emit(DataState.Error(emitValue, requestId))
    }
}

fun <T> dataStateFlow(
    requestId: Int,
    block: suspend () -> T
): Flow<DataState<DataModel<T>>> {
    return flow {
        emit(DataState.Loading(requestId))

        val result = try {
            block.invoke()
        } catch (e: ApiErrorException) {
            emit(DataState.ApiError(e, requestId))
            return@flow
        } catch (e: Exception) {
            emit(DataState.Error(e, requestId))
            return@flow
        }

        emit(DataState.Success(DataModel(result), requestId))

    }
}

fun <T> dataStateSuccessFlow(
    requestId: Int,
    block: suspend () -> T?
): Flow<DataState<T>> {
    return flow {
        emit(DataState.Loading(requestId))
        kotlinx.coroutines.delay(10)
        emit(DataState.Success(block.invoke(), requestId))
    }
}

fun <T> dataStateDataModelSuccessFlow(
    requestId: Int,
    block: suspend () -> T
): Flow<DataState<DataModel<T>>> {
    return dataStateSuccessFlow(requestId) {
        DataModel(block.invoke()!!)
    }
}

val DataState.ApiError.error get() = apiErrorException.errorModel.message()
val DataState.Error.error get() = throwable.message

val DataState<*>.errorMessage: String?
    get() {
        return when (this) {
            is DataState.ApiError -> error
            is DataState.Error -> error
            else -> null
        }
    }

fun <T> Flow<DataState<T>>.filterNotLoading(): Flow<DataState<T>> {
    return filter { it !is DataState.Loading }
}

// Type check extensions
val DataState<*>.isSuccess: Boolean get() = this is DataState.Success
val DataState<*>.isError: Boolean get() = this is DataState.Error
val DataState<*>.isApiError: Boolean get() = this is DataState.ApiError
val DataState<*>.isLoading: Boolean get() = this is DataState.Loading
val DataState<*>.isAnyError: Boolean get() = this is DataState.Error || this is DataState.ApiError


fun <M, E : BaseApiError> NeverErrorObservable<M>.requestToFlow(
    requestId: Int,
    eClass: Class<E>
): Flow<DataState<M>> {
    return callbackFlow {
        trySend(DataState.Loading( requestId))
        val subscribe = successResponse {
            trySend(DataState.Success(it.body(), requestId))
        }
            .timeoutException {
                trySend(DataState.Error(it, requestId))
            }
            .connectionException {
                trySend(DataState.Error(it, requestId))
            }
            .apiException({
                if (it != null) {
                    val apiErrorException = ApiErrorException(it as BaseApiError)
                    trySend(DataState.ApiError(apiErrorException, requestId))
                }
            }, eClass)
            .exception {
                trySend(DataState.Error(it, requestId))
            }
            .subscribe()

        awaitClose {
            subscribe.dispose()
        }

    }
}

fun <T> Flow<DataState<T>>.toUiDataFlow(): Flow<UiData<T>> =
    map { dataState ->
        val extractedData = when (dataState) {
            is DataState.Success -> dataState.data
            else -> null
        }
        UiData(
            data = extractedData,
            state = dataState
        )
    }

fun <T> DataState<T>?.shouldShowEmptyState(): Boolean {
    return this is DataState.Success && (data as? List<*>)?.isEmpty() == true
}

