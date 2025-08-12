package com.hive.exts

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.hive.DataState
import kotlin.let

@Composable
fun <T> HandleDataStateErrors(
    dataState: DataState<T>?,
    onErrorHandled: (() -> Unit)? = null
) {
    val context = LocalContext.current

    LaunchedEffect(dataState) {
        when (dataState) {
            is DataState.ApiError -> {
                Toast.makeText(
                    context,
                    dataState.apiErrorException.getUserFriendlyMessage(),
                    Toast.LENGTH_SHORT
                ).show()
                onErrorHandled?.invoke()
            }
            is DataState.Error -> {
                Toast.makeText(
                    context,
                    dataState.getUserFriendlyMessage(),
                    Toast.LENGTH_SHORT
                ).show()
                onErrorHandled?.invoke()
            }
            else -> {
                
            }
        }
    }
}

@Composable
fun <T> HandleDataStateErrorsWithDialog(
    dataState: DataState<T>?,
    onDismiss: () -> Unit,
    showRetryButton: Boolean = true,
    onRetry: (() -> Unit)? = null,
    dialogTitle: String = "Error",
    retryButtonText: String = "Retry",
    dismissButtonText: String = "OK"
) {
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(dataState) {
        when (dataState) {
            is DataState.ApiError -> {
                errorMessage = dataState.apiErrorException.getUserFriendlyMessage()
                showDialog = true
            }
            is DataState.Error -> {
                errorMessage = dataState.getUserFriendlyMessage()
                showDialog = true
            }
            else -> {
                showDialog = false
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            title = {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                if (showRetryButton && onRetry != null) {
                    TextButton(
                        onClick = {
                            showDialog = false
                            onRetry()
                        }
                    ) {
                        Text(retryButtonText)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDismiss()
                    }
                ) {
                    Text(dismissButtonText)
                }
            }
        )
    }
}

@Composable
fun <T> HandleFieldErrorsWithToast(
    dataState: DataState<T>?,
    fieldName: String,
    onErrorHandled: (() -> Unit)? = null
) {
    val context = LocalContext.current

    LaunchedEffect(dataState) {
        dataState?.getFieldErrorMessage(fieldName)?.let { errorMessage ->
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_SHORT
            ).show()
            onErrorHandled?.invoke()
        }
    }
}

@Composable
fun <T> HandleSpecificErrors(
    dataState: DataState<T>?,
    onEmailError: ((String) -> Unit)? = null,
    onPasswordError: ((String) -> Unit)? = null,
    onMobileError: ((String) -> Unit)? = null,
    onNameError: ((String) -> Unit)? = null,
    onNetworkError: ((String) -> Unit)? = null,
    onTimeoutError: ((String) -> Unit)? = null,
    onGenericError: ((String) -> Unit)? = null,
    onErrorHandled: (() -> Unit)? = null
) {
    LaunchedEffect(dataState) {
        dataState?.let { state ->
            ApiErrorUtils.handleCommonFieldErrors(
                dataState = state,
                onEmailError = onEmailError,
                onPasswordError = onPasswordError,
                onMobileError = onMobileError,
                onNameError = onNameError,
                onNetworkError = onNetworkError,
                onTimeoutError = onTimeoutError,
                onGenericError = onGenericError
            )

            
            if (state.isApiError() || state.isError()) {
                onErrorHandled?.invoke()
            }
        }
    }
}

@Composable
fun <T> HandleDataStateLoading(
    dataState: DataState<T>?,
) {

    ShowLoading(show = dataState is DataState.Loading,)
}


@Composable
fun <T> HandleDataState(
    dataState: DataState<T>?,
    onSuccess: ((T?) -> Unit)? = null,
    showErrorAsToast: Boolean = true,
    showErrorAsDialog: Boolean = false,
    onErrorHandled: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    dialogTitle: String = "Error",
    showLoadingIndicator: Boolean = true,
) {
    
    LaunchedEffect(dataState) {
        if (dataState is DataState.Success) {
            onSuccess?.invoke(dataState.data)
        }
    }

    
    if (showLoadingIndicator) {
        HandleDataStateLoading(
            dataState = dataState,
        )
    }

    
    if (showErrorAsToast) {
        HandleDataStateErrors(
            dataState = dataState,
            onErrorHandled = onErrorHandled
        )
    }

    
    if (showErrorAsDialog) {
        HandleDataStateErrorsWithDialog(
            dataState = dataState,
            onDismiss = { onErrorHandled?.invoke() },
            onRetry = onRetry,
            dialogTitle = dialogTitle
        )
    }
}