package com.hive

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


class SplashViewModel : ViewModel() {

    val user = flowOf(null)
        .map {
            "Andre" to 2
        }


    fun isOdd(value: Int): Boolean {
        return value % 2 != 0
    }
}
