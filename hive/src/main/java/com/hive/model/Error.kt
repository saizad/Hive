package com.hive.model

import com.google.gson.annotations.SerializedName

data class Error(
    val description: String,
    val error: String,
    @SerializedName("error_code")
    val errorCode: Int,
    val message: String,
    val status: Int
) {
    var fields: List<FieldError>? = null
}