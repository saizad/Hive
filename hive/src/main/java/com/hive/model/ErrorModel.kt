package com.hive.model

data class ErrorModel(val error: Error) : BaseApiError() {

    override fun error(): String {
        return error.error
    }

    override fun message(): String {
        return error.description
    }

    companion object {
        fun generateErrorMessage(errorModel: ErrorModel): String {
            errorModel.error.fields?.let {
                val fields = StringBuilder()
                it.forEach {
                    fields.append(it.field).append(", ")
                }
                val fieldNames = fields.toString().trim()
                return if (it.size > 1) {
                    "$fieldNames fields are missing"
                } else {
                    "$fieldNames field is missing"
                }
            }
            return errorModel.error.description
        }
    }
}