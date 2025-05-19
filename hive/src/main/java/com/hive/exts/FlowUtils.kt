package com.hive.exts

import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> {
    require(periodMillis > 0) { "period should be positive" }
    return channelFlow {
        var lastEmissionTime = 0L
        collect { value ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastEmissionTime >= periodMillis) {
                lastEmissionTime = currentTime
                send(value)  // Use `send` instead of `emit` inside `channelFlow`
            }
        }
    }.flowOn(Dispatchers.Default) // Ensures proper timing behavior
}


fun <T1, T2> Flow<T1>.combineFirst(flow: Flow<T2>): Flow<T1> {
    return combine(flow) { a: T1, _: T2 -> a }
}

fun <T1, T2> Flow<T1>.combineSecond(flow: Flow<T2>): Flow<T2> {
    return combine(flow) { _: T1, b: T2 -> b }
}

fun <T1, T2> Flow<T1>.combinePair(flow: Flow<T2>): Flow<Pair<T1, T2>> {
    return combine(flow) { a: T1, b: T2 -> a to b }
}

suspend fun <T1, T2> Flow<T1>.combineCollect(
    flow: Flow<T2>,
    transform: suspend (a: T1, b: T2) -> Unit
) {
    combine(flow) { a: T1, b: T2 -> transform.invoke(a, b) }.collect()
}

val <T> Observable<T>.toFlow: Flow<T>
    get() {
        val block: suspend ProducerScope<T>.() -> Unit = {
            val request =
                subscribe({
                    it.alsoPrint("!!")
                    trySend(it)
                }, {
                    it.alsoPrint("$$")
                    close(it)
                }, {
                    close()
                })

            awaitClose {
                request.dispose()
            }
        }
        return callbackFlow(block)
    }

fun intervalFlow(duration: Long, interval: Long): Flow<Long> = flow {
    val endTime = System.currentTimeMillis() + duration
    while (System.currentTimeMillis() < endTime) {
        emit(endTime - System.currentTimeMillis())
        delay(interval)
    }
}


fun intervalFlow(interval: Long): Flow<Unit> = flow {
    while (true) {
        emit(Unit) // Emits a value
        delay(interval) // Waits before emitting the next value
    }
}


val MutableStateFlow<Int>.emitRefresh: Unit
    get() {
        value = value.inc()
    }

