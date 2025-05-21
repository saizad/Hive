package com.hive

import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

abstract class Environment<U>(
    val locale: MutableStateFlow<Locale>,
    val currentUser: CurrentUser<U>,
    val networkRequest: NetworkRequest,
)