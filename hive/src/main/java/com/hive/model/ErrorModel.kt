package com.hive.model

data class ErrorModel(val error: Error) : BaseApiError() {

    override fun error(): String {
        return error.error
    }

    override fun message(): String {
        return error.description
    }

}