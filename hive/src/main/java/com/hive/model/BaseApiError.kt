package com.hive.model

abstract class BaseApiError {
    abstract fun error(): String
    abstract fun message(): String
}