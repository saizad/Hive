package com.hive

data class UiData<T>(
    val data: T? = null,
    val state: DataState<T>? = null
)
