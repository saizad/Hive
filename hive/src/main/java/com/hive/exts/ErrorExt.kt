package com.hive.exts

import com.hive.ApiErrorException
import com.hive.DataState
import com.hive.model.Error
import com.hive.model.ErrorModel
import com.hive.model.FieldError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlin.collections.any
import kotlin.collections.associate
import kotlin.collections.filter
import kotlin.collections.find
import kotlin.collections.firstOrNull
import kotlin.collections.flatMap
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.isNullOrEmpty
import kotlin.collections.joinToString
import kotlin.collections.mapNotNull
import kotlin.collections.toMap
import kotlin.jvm.javaClass
import kotlin.let
import kotlin.stackTraceToString
import kotlin.takeIf
import kotlin.text.contains
import kotlin.text.isNotBlank
import kotlin.text.isNotEmpty
import kotlin.text.isNullOrBlank
import kotlin.to
import kotlin.toString


fun FieldError.getFirstMessage(): String? {
    return when (val msg = message) {
        is List<*> -> msg.firstOrNull()?.toString()
        is Map<*, *> -> {
            // Get first nested field's first message
            msg.values.firstOrNull()?.let { nestedMessages ->
                when (nestedMessages) {
                    is List<*> -> nestedMessages.firstOrNull()?.toString()
                    else -> nestedMessages.toString()
                }
            }
        }
        else -> null
    }
}

fun FieldError.getAllMessages(): String {
    return when (val msg = message) {
        is List<*> -> msg.mapNotNull { it?.toString() }.joinToString(", ")
        is Map<*, *> -> {
            msg.entries.joinToString("; ") { (key, value) ->
                val nestedMessages = when (value) {
                    is List<*> -> value.mapNotNull { it?.toString() }.joinToString(", ")
                    else -> value.toString()
                }
                "$key: $nestedMessages"
            }
        }
        else -> ""
    }
}

fun FieldError.hasMessages(): Boolean {
    return when (val msg = message) {
        is List<*> -> msg.isNotEmpty()
        is Map<*, *> -> msg.isNotEmpty()
        else -> msg != null
    }
}

fun FieldError.getNestedFieldErrors(): Map<String, List<String>>? {
    return when (val msg = message) {
        is Map<*, *> -> {
            msg.mapNotNull { (key, value) ->
                val keyStr = key?.toString()
                val valueList = when (value) {
                    is List<*> -> value.mapNotNull { it?.toString() }
                    else -> listOf(value.toString())
                }
                if (keyStr != null) keyStr to valueList else null
            }.toMap()
        }
        else -> null
    }
}

fun FieldError.isNestedFieldError(): Boolean = message is Map<*, *>

fun FieldError.isSimpleFieldError(): Boolean = message is List<*>

fun FieldError.getNestedFieldError(nestedFieldName: String): List<String>? {
    return getNestedFieldErrors()?.get(nestedFieldName)
}

fun FieldError.hasNestedFieldError(nestedFieldName: String): Boolean {
    return getNestedFieldErrors()?.containsKey(nestedFieldName) == true
}

// Extensions for Error
fun Error.hasFieldErrors(): Boolean = !fields.isNullOrEmpty()

fun Error.getFieldError(fieldName: String): FieldError? =
    fields?.find { it.field == fieldName }

fun Error.getFieldErrors(vararg fieldNames: String): List<FieldError> =
    fields?.filter { fieldError ->
        fieldNames.any { it == fieldError.field }
    } ?: emptyList()

fun Error.getAllFieldNames(): List<String> =
    fields?.mapNotNull { it.field } ?: emptyList()

fun Error.getFieldErrorMessages(): Map<String, List<String>> =
    fields?.associate { fieldError ->
        val fieldName = fieldError.field ?: "unknown"
        val messages = when (val msg = fieldError.message) {
            is List<*> -> msg.mapNotNull { it?.toString() }
            is Map<*, *> -> {
                // Flatten nested messages
                msg.values.flatMap { value ->
                    when (value) {
                        is List<*> -> value.mapNotNull { it?.toString() }
                        else -> listOf(value.toString())
                    }
                }
            }
            else -> emptyList()
        }
        fieldName to messages
    } ?: emptyMap()

fun Error.getNestedFieldError(fieldName: String, nestedFieldName: String): List<String>? =
    getFieldError(fieldName)?.getNestedFieldError(nestedFieldName)

fun Error.hasNestedFieldError(fieldName: String, nestedFieldName: String): Boolean =
    getFieldError(fieldName)?.hasNestedFieldError(nestedFieldName) == true

fun Error.isValidationError(): Boolean = errorCode == 21 || error.contains("field", ignoreCase = true)

fun Error.getAllNestedFieldErrors(): Map<String, Map<String, List<String>>> {
    return fields?.mapNotNull { fieldError ->
        val fieldName = fieldError.field
        val nestedErrors = fieldError.getNestedFieldErrors()
        if (fieldName != null && nestedErrors != null) {
            fieldName to nestedErrors
        } else null
    }?.toMap() ?: emptyMap()
}

// Extensions for ErrorModel
fun ErrorModel.getFieldErrorMessage(fieldName: String): String? =
    error.getFieldError(fieldName)?.getFirstMessage()

fun ErrorModel.getAllFieldErrorMessages(fieldName: String): List<String> {
    val fieldError = error.getFieldError(fieldName) ?: return emptyList()
    return when (val msg = fieldError.message) {
        is List<*> -> msg.mapNotNull { it?.toString() }
        is Map<*, *> -> {
            // Flatten nested messages into a single list
            msg.values.flatMap { value ->
                when (value) {
                    is List<*> -> value.mapNotNull { it?.toString() }
                    else -> listOf(value.toString())
                }
            }
        }
        else -> emptyList()
    }
}

fun ErrorModel.hasFieldError(fieldName: String): Boolean =
    error.getFieldError(fieldName) != null

fun ErrorModel.getNestedFieldErrorMessage(fieldName: String, nestedFieldName: String): String? =
    error.getNestedFieldError(fieldName, nestedFieldName)?.firstOrNull()

fun ErrorModel.getAllNestedFieldErrorMessages(fieldName: String, nestedFieldName: String): List<String> =
    error.getNestedFieldError(fieldName, nestedFieldName) ?: emptyList()

fun ErrorModel.hasNestedFieldError(fieldName: String, nestedFieldName: String): Boolean =
    error.hasNestedFieldError(fieldName, nestedFieldName)

fun ErrorModel.getFormattedFieldErrors(): String {
    if (!error.hasFieldErrors()) return error.description

    val fieldErrors = error.fields!!
    return fieldErrors.joinToString("\n") { fieldError ->
        val fieldName = fieldError.field ?: "field"

        when {
            fieldError.isNestedFieldError() -> {
                // Handle nested field errors like profile.presently_doing
                val nestedErrors = fieldError.getNestedFieldErrors()!!
                nestedErrors.entries.joinToString("\n") { (nestedField, messages) ->
                    "$fieldName.$nestedField: ${messages.joinToString(", ")}"
                }
            }
            fieldError.isSimpleFieldError() -> {
                // Handle simple field errors like token
                val messages = fieldError.getAllMessages()
                if (messages.isNotEmpty()) {
                    "$fieldName: $messages"
                } else {
                    "$fieldName field has an error"
                }
            }
            else -> {
                "$fieldName field has an error"
            }
        }
    }
}

fun ErrorModel.getUserFriendlyMessage(): String {
    return when {
        error.isValidationError() && error.hasFieldErrors() -> getFormattedFieldErrors()
        error.description.isNotBlank() -> error.description
        error.message.isNotBlank() -> error.message
        else -> "An unexpected error occurred"
    }
}

// Extensions for ApiErrorException
fun ApiErrorException.isFieldError(): Boolean =
    (errorModel as? ErrorModel)?.error?.hasFieldErrors() == true

fun ApiErrorException.getFieldErrorMessage(fieldName: String): String? =
    (errorModel as? ErrorModel)?.getFieldErrorMessage(fieldName)

fun ApiErrorException.hasFieldError(fieldName: String): Boolean =
    (errorModel as? ErrorModel)?.hasFieldError(fieldName) == true

fun ApiErrorException.getUserFriendlyMessage(): String =
    (errorModel as? ErrorModel)?.getUserFriendlyMessage() ?: message ?: "Unknown error"

fun ApiErrorException.getErrorCode(): Int? =
    (errorModel as? ErrorModel)?.error?.errorCode

fun ApiErrorException.getHttpStatus(): Int? =
    (errorModel as? ErrorModel)?.error?.status

fun ApiErrorException.getNestedFieldErrorMessage(fieldName: String, nestedFieldName: String): String? =
    (errorModel as? ErrorModel)?.getNestedFieldErrorMessage(fieldName, nestedFieldName)

fun ApiErrorException.hasNestedFieldError(fieldName: String, nestedFieldName: String): Boolean =
    (errorModel as? ErrorModel)?.hasNestedFieldError(fieldName, nestedFieldName) == true

// Extensions for DataState
fun <T> DataState<T>.isSuccess(): Boolean = this is DataState.Success

fun <T> DataState<T>.isError(): Boolean = this is DataState.Error

fun <T> DataState<T>.isApiError(): Boolean = this is DataState.ApiError

fun <T> DataState<T>.isLoading(): Boolean = this is DataState.Loading

// Extensions for DataState.Error specifically
fun DataState.Error.isNetworkError(): Boolean = ApiErrorUtils.isNetworkError(throwable)

fun DataState.Error.isTimeoutError(): Boolean = ApiErrorUtils.isTimeoutError(throwable)

fun DataState.Error.getUserFriendlyMessage(): String = ApiErrorUtils.getGenericErrorMessage(throwable)

fun DataState.Error.getCause(): Throwable? = throwable.cause

fun DataState.Error.getStackTrace(): String = throwable.stackTraceToString()

fun DataState.Error.isOfType(exceptionClass: Class<out Throwable>): Boolean =
    exceptionClass.isInstance(throwable)

inline fun <reified T : Throwable> DataState.Error.isOfType(): Boolean = throwable is T

fun DataState.Error.hasMessage(): Boolean = !throwable.message.isNullOrBlank()

fun DataState.Error.getMessageOrDefault(defaultMessage: String = "Unknown error"): String =
    throwable.message?.takeIf { it.isNotBlank() } ?: defaultMessage

fun <T> DataState<T>.getSuccessData(): T? = (this as? DataState.Success)?.data

fun <T> DataState<T>.getApiError(): ApiErrorException? = (this as? DataState.ApiError)?.apiErrorException

fun <T> DataState<T>.getGenericError(): Throwable? = (this as? DataState.Error)?.throwable

// Enhanced error extraction with specific DataState.Error extensions
fun <T> DataState<T>.getGenericErrorDetails(): DataState.Error? = this as? DataState.Error

fun <T> DataState<T>.isNetworkError(): Boolean =
    (this as? DataState.Error)?.isNetworkError() == true

fun <T> DataState<T>.isTimeoutError(): Boolean =
    (this as? DataState.Error)?.isTimeoutError() == true

fun <T> DataState<T>.getErrorMessage(): String? = when (this) {
    is DataState.ApiError -> apiErrorException.getUserFriendlyMessage()
    is DataState.Error -> this.getUserFriendlyMessage()
    else -> null
}

fun <T> DataState<T>.hasFieldError(fieldName: String): Boolean =
    getApiError()?.hasFieldError(fieldName) == true

fun <T> DataState<T>.getFieldErrorMessage(fieldName: String): String? =
    getApiError()?.getFieldErrorMessage(fieldName)

fun <T> DataState<T>.hasNestedFieldError(fieldName: String, nestedFieldName: String): Boolean =
    getApiError()?.hasNestedFieldError(fieldName, nestedFieldName) == true

fun <T> DataState<T>.getNestedFieldErrorMessage(fieldName: String, nestedFieldName: String): String? =
    getApiError()?.getNestedFieldErrorMessage(fieldName, nestedFieldName)


inline fun <T : Any, R> Flow<DataState<T?>>.flatMapSuccessTo(
    crossinline nextCall: (T) -> Flow<DataState<R>>
): Flow<DataState<R>> = flatMapConcat { dataState ->
    when (dataState) {
        is DataState.Success -> {
            val data = dataState.data
            if (data != null) {
                nextCall(data)
            } else {
                flowOf(
                    DataState.Error(
                        IllegalStateException("Expected non-null data in DataState.Success but got null."),
                        dataState.requestId
                    )
                )
            }
        }
        is DataState.Error -> {
            flowOf(dataState)
        }
        is DataState.ApiError -> {
            flowOf(dataState)
        }
        is DataState.Loading -> {
            flowOf(dataState)
        }
    }
}


// Utility functions for common error handling patterns
object ApiErrorUtils {

    fun handleCommonFieldErrors(
        dataState: DataState<*>,
        onEmailError: ((String) -> Unit)? = null,
        onPasswordError: ((String) -> Unit)? = null,
        onMobileError: ((String) -> Unit)? = null,
        onNameError: ((String) -> Unit)? = null,
        onTokenError: ((String) -> Unit)? = null,
        onProfileError: ((String) -> Unit)? = null,
        onNestedFieldError: ((fieldName: String, nestedFieldName: String, message: String) -> Unit)? = null,
        onNetworkError: ((String) -> Unit)? = null,
        onTimeoutError: ((String) -> Unit)? = null,
        onGenericError: ((String) -> Unit)? = null
    ) {
        when {
            dataState.hasFieldError("email") -> {
                onEmailError?.invoke(dataState.getFieldErrorMessage("email") ?: "Email error")
            }
            dataState.hasFieldError("password") -> {
                onPasswordError?.invoke(dataState.getFieldErrorMessage("password") ?: "Password error")
            }
            dataState.hasFieldError("mobile") -> {
                onMobileError?.invoke(dataState.getFieldErrorMessage("mobile") ?: "Mobile error")
            }
            dataState.hasFieldError("name") -> {
                onNameError?.invoke(dataState.getFieldErrorMessage("name") ?: "Name error")
            }
            dataState.hasFieldError("token") -> {
                onTokenError?.invoke(dataState.getFieldErrorMessage("token") ?: "Token error")
            }
            dataState.hasFieldError("profile") -> {
                onProfileError?.invoke(dataState.getFieldErrorMessage("profile") ?: "Profile error")
            }
            dataState.hasNestedFieldError("profile", "presently_doing") -> {
                val message = dataState.getNestedFieldErrorMessage("profile", "presently_doing") ?: "Profile validation error"
                onNestedFieldError?.invoke("profile", "presently_doing", message)
            }
            dataState.isNetworkError() -> {
                onNetworkError?.invoke("Please check your internet connection and try again")
            }
            dataState.isTimeoutError() -> {
                onTimeoutError?.invoke("Request timed out. Please try again")
            }
            else -> {
                onGenericError?.invoke(dataState.getErrorMessage() ?: "Unknown error")
            }
        }
    }

    fun handleNestedFieldErrors(
        dataState: DataState<*>,
        fieldName: String,
        nestedFieldHandlers: Map<String, (String) -> Unit>,
        onGenericFieldError: ((String) -> Unit)? = null
    ) {
        if (!dataState.hasFieldError(fieldName)) return

        val apiError = dataState.getApiError() ?: return
        val errorModel = apiError.errorModel as? ErrorModel ?: return
        val fieldError = errorModel.error.getFieldError(fieldName) ?: return

        if (fieldError.isNestedFieldError()) {
            val nestedErrors = fieldError.getNestedFieldErrors()!!
            nestedErrors.forEach { (nestedField, messages) ->
                val handler = nestedFieldHandlers[nestedField]
                if (handler != null) {
                    handler.invoke(messages.firstOrNull() ?: "Error in $nestedField")
                } else {
                    onGenericFieldError?.invoke("$nestedField: ${messages.joinToString(", ")}")
                }
            }
        } else {
            onGenericFieldError?.invoke(fieldError.getFirstMessage() ?: "Error in $fieldName")
        }
    }

    fun isNetworkError(throwable: Throwable): Boolean {
        return throwable.javaClass.simpleName.contains("Network", ignoreCase = true) ||
                throwable.javaClass.simpleName.contains("Connection", ignoreCase = true) ||
                throwable.message?.contains("network", ignoreCase = true) == true ||
                throwable.message?.contains("connection", ignoreCase = true) == true
    }

    fun isTimeoutError(throwable: Throwable): Boolean {
        return throwable.javaClass.simpleName.contains("Timeout", ignoreCase = true) ||
                throwable.message?.contains("timeout", ignoreCase = true) == true
    }

    fun getGenericErrorMessage(throwable: Throwable): String {
        return when {
            isNetworkError(throwable) -> "Please check your internet connection and try again"
            isTimeoutError(throwable) -> "Request timed out. Please try again"
            else -> throwable.message ?: "An unexpected error occurred"
        }
    }
}

// Enhanced ErrorModel companion object methods
fun ErrorModel.Companion.generateDetailedErrorMessage(errorModel: ErrorModel): String {
    return errorModel.getUserFriendlyMessage()
}

fun ErrorModel.Companion.extractFieldNames(errorModel: ErrorModel): List<String> {
    return errorModel.error.getAllFieldNames()
}

fun ErrorModel.Companion.hasSpecificField(errorModel: ErrorModel, fieldName: String): Boolean {
    return errorModel.hasFieldError(fieldName)
}

// Type aliases for cleaner code
typealias ApiResult<T> = DataState<T>
typealias ApiSuccess<T> = DataState.Success<T>
typealias ApiFailure = DataState.ApiError
typealias GenericFailure = DataState.Error
typealias ApiLoading = DataState.Loading

// Extension functions for Result-like behavior
inline fun <T> DataState<T>.onSuccess(action: (T?) -> Unit): DataState<T> {
    if (this is DataState.Success) action(data)
    return this
}

inline fun <T> DataState<T>.onApiError(action: (ApiErrorException) -> Unit): DataState<T> {
    if (this is DataState.ApiError) action(apiErrorException)
    return this
}

inline fun <T> DataState<T>.onError(action: (Throwable) -> Unit): DataState<T> {
    if (this is DataState.Error) action(throwable)
    return this
}

inline fun <T> DataState<T>.onLoading(action: () -> Unit): DataState<T> {
    if (this is DataState.Loading) action()
    return this
}

inline fun <T> DataState<T>.onAnyError(action: (String) -> Unit): DataState<T> {
    getErrorMessage()?.let(action)
    return this
}