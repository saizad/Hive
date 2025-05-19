package com.hive.model

data class FCMTokenBody @JvmOverloads constructor(
    val name: String,
    val deviceId: String,
    val registrationId: String,
    val active: Boolean = true,
    val type: String = "android"
)