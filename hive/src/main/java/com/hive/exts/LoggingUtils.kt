package com.hive.exts

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach


inline fun <T> T.alsoLog(tag: String? = null, block: (T) -> String = { it.toString() }): T {
    Log.d(tag ?: "Print", block(this))
    return this
}

fun <T> T.alsoLog(tag: String? = null): T {
    alsoLog(tag) {
        it.toString()
    }
    return this
}

fun <T> Flow<T>.log(tag: String? = null, message: (T) -> String = { it.toString() }): Flow<T> {
    return onEach { it.alsoLog(tag, message) }
}


inline fun <T> T.alsoPrint(tag: String? = null, block: (T) -> String = { it.toString() }): T {
    println("${tag ?: "Print"} -> ${block(this)}")
    return this
}

fun <T> T.alsoPrint(tag: String? = null): T {
    alsoPrint(tag) {
        it.toString()
    }
    return this
}

fun <T> Flow<T>.print(tag: String? = null, message: (T) -> String = { it.toString() }): Flow<T> {
    return onEach { it.alsoPrint(tag, message) }
}
