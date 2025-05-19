package com.hive

import kotlinx.coroutines.flow.Flow

interface CurrentUser<U> {

    fun login(newUser: U)

    fun logout(): Flow<Unit>

    fun isLoggedIn(): Flow<Boolean>

    fun currentUser(): Flow<U?>

    fun loggedInUser(): Flow<U>

    fun loggedOutUser(): Flow<Unit?>

    suspend fun authLogin(user: U, refreshToken: String?, accessToken: String?)

    fun refreshToken(): Flow<String?>

    fun accessToken(): Flow<String?>
}