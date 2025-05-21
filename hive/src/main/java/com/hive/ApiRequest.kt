package com.hive

data class ApiRequest<out R>(val dataState: DataState<R>)