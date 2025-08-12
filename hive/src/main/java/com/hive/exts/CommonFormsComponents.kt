package com.hive.exts


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulse.field.FormField
import com.pulse.field.error

/**
 * CommonForms - A comprehensive UI library for form components
 *
 * This library provides:
 * - Type-safe form field components that integrate with PulseField validation
 * - Extensible theming and styling system
 * - Consistent Material 3 design implementation
 * - Accessibility support with test tags
 * - Reactive validation state management
 *
 * Architecture:
 * - Core: Base components and interfaces
 * - Components: Specific form field implementations
 * - Theming: Styling and color management
 * - Utils: Helper functions and extensions
 */

/**
 * Test tag constants for automated testing
 */
object FormTestTags {
    const val FIELD_TITLE = "field_title"
    const val TEXT_FIELD = "text_field"
    const val DROPDOWN_FIELD = "dropdown_field"
    const val SEARCH_DROPDOWN_FIELD = "search_dropdown_field"
    const val ERROR_TEXT = "error_text"
    const val MANDATORY_INDICATOR = "mandatory_indicator"
    const val SUPPORTING_TEXT = "supporting_text"
    const val LEADING_ICON = "leading_icon"
    const val TRAILING_ICON = "trailing_icon"
}

/**
 * Validation state representation for UI components
 */
@Stable
sealed class FieldValidationState {
    object Valid : FieldValidationState()
    data class Invalid(val errorMessage: String) : FieldValidationState()
    object Pristine : FieldValidationState()
}

/**
 * Configuration for form field appearance and behavior
 */
@Stable
data class FormFieldConfig(
    val isEnabled: Boolean = true,
    val isReadOnly: Boolean = false,
    val isSingleLine: Boolean = false,
    val maxLines: Int = if (isSingleLine) 1 else Int.MAX_VALUE,
    val minLines: Int = 1,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val shape: Shape = RoundedCornerShape(CornerSize(10.dp)),
    val colors: FormFieldColors? = null,
    val textStyle: TextStyle? = null,
    val contentPadding: Dp? = null
)

/**
 * Theming configuration for form field colors
 * Provides different color states based on validation and interaction
 */
@Stable
data class FormFieldColors(
    val focusedBorderColor: Color,
    val unfocusedBorderColor: Color,
    val errorBorderColor: Color,
    val validBorderColor: Color,
    val focusedContainerColor: Color,
    val unfocusedContainerColor: Color,
    val errorContainerColor: Color,
    val validContainerColor: Color,
    val textColor: Color,
    val placeholderColor: Color,
    val errorTextColor: Color,
    val labelColor: Color,
    val mandatoryIndicatorColor: Color
) {
    companion object {
        /**
         * Creates default colors using Material 3 color scheme
         */
        @Composable
        fun default(
            validColor: Color = MaterialTheme.colorScheme.tertiary,
            errorColor: Color = MaterialTheme.colorScheme.error,
            primaryColor: Color = MaterialTheme.colorScheme.primary
        ): FormFieldColors = FormFieldColors(
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = errorColor,
            validBorderColor = validColor,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            validContainerColor = MaterialTheme.colorScheme.surface,
            textColor = MaterialTheme.colorScheme.onSurface,
            placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorTextColor = errorColor,
            labelColor = MaterialTheme.colorScheme.onSurface,
            mandatoryIndicatorColor = primaryColor
        )
    }

    /**
     * Converts FormFieldColors to Material 3 TextFieldColors based on validation state
     */
    @Composable
    fun toTextFieldColors(validationState: FieldValidationState): TextFieldColors {
        return when (validationState) {
            is FieldValidationState.Valid -> OutlinedTextFieldDefaults.colors(
                focusedBorderColor = validBorderColor,
                unfocusedBorderColor = validBorderColor,
                focusedContainerColor = validContainerColor,
                unfocusedContainerColor = validContainerColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                unfocusedPlaceholderColor = placeholderColor,
                focusedPlaceholderColor = placeholderColor
            )
            is FieldValidationState.Invalid -> OutlinedTextFieldDefaults.colors(
                focusedBorderColor = errorBorderColor,
                unfocusedBorderColor = errorBorderColor,
                errorBorderColor = errorBorderColor,
                focusedContainerColor = errorContainerColor,
                unfocusedContainerColor = errorContainerColor,
                errorContainerColor = errorContainerColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                errorTextColor = errorTextColor,
                unfocusedPlaceholderColor = placeholderColor,
                focusedPlaceholderColor = placeholderColor
            )
            is FieldValidationState.Pristine -> OutlinedTextFieldDefaults.colors(
                focusedBorderColor = focusedBorderColor,
                unfocusedBorderColor = unfocusedBorderColor,
                focusedContainerColor = focusedContainerColor,
                unfocusedContainerColor = unfocusedContainerColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                unfocusedPlaceholderColor = placeholderColor,
                focusedPlaceholderColor = placeholderColor
            )
        }
    }
}

/**
 * Base interface for field value converters
 * Handles conversion between field data type and UI display format
 */
interface FieldValueConverter<T> {
    /**
     * Convert field value to display string
     */
    fun valueToString(value: T?): String

    /**
     * Convert display string to field value
     */
    fun stringToValue(text: String): T?

    /**
     * Validate the string representation before conversion
     */
    fun isValidString(text: String): Boolean = true
}

/**
 * Common field value converters
 */
object FieldConverters {
    val String: FieldValueConverter<String> = object : FieldValueConverter<String> {
        override fun valueToString(value: String?): String = value ?: ""
        override fun stringToValue(text: String): String = text
    }

    val Int: FieldValueConverter<Int> = object : FieldValueConverter<Int> {
        override fun valueToString(value: Int?): String = value?.toString() ?: ""
        override fun stringToValue(text: String): Int? = text.toIntOrNull()
        override fun isValidString(text: String): Boolean = text.isEmpty() || text.toIntOrNull() != null
    }

    val Long: FieldValueConverter<Long> = object : FieldValueConverter<Long> {
        override fun valueToString(value: Long?): String = value?.toString() ?: ""
        override fun stringToValue(text: String): Long? = text.toLongOrNull()
        override fun isValidString(text: String): Boolean = text.isEmpty() || text.toLongOrNull() != null
    }

    val Double: FieldValueConverter<Double> = object : FieldValueConverter<Double> {
        override fun valueToString(value: Double?): String = value?.toString() ?: ""
        override fun stringToValue(text: String): Double? = text.toDoubleOrNull()
        override fun isValidString(text: String): Boolean = text.isEmpty() || text.toDoubleOrNull() != null
    }

    val Float: FieldValueConverter<Float> = object : FieldValueConverter<Float> {
        override fun valueToString(value: Float?): String = value?.toString() ?: ""
        override fun stringToValue(text: String): Float? = text.toFloatOrNull()
        override fun isValidString(text: String): Boolean = text.isEmpty() || text.toFloatOrNull() != null
    }
}

/**
 * Field title component with optional mandatory indicator
 *
 * @param title The title text to display
 * @param field The form field to check for mandatory status
 * @param modifier Modifier for customization
 * @param colors Color configuration for the title
 * @param textStyle Text style for the title
 * @param mandatoryIndicator Custom composable for mandatory indicator (defaults to "*")
 */
@Composable
fun <T> FieldTitle(
    title: String,
    field: FormField<T>,
    modifier: Modifier = Modifier,
    colors: FormFieldColors = FormFieldColors.default(),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
    mandatoryIndicator: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .testTag(FormTestTags.FIELD_TITLE)
    ) {
        Text(
            text = title,
            color = colors.labelColor,
            style = textStyle
        )

        if (field.isMandatory) {
            if (mandatoryIndicator != null) {
                mandatoryIndicator()
            } else {
                Text(
                    text = " *",
                    color = colors.mandatoryIndicatorColor,
                    style = textStyle,
                    modifier = Modifier.testTag(FormTestTags.MANDATORY_INDICATOR)
                )
            }
        }
    }
}

/**
 * Base outlined text field component with consistent styling
 */
@Composable
fun BaseOutlinedTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    config: FormFieldConfig = FormFieldConfig(),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    isError: Boolean = false,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(config.shape)
            .testTag(FormTestTags.TEXT_FIELD),
        enabled = config.isEnabled,
        readOnly = config.isReadOnly,
        textStyle = config.textStyle ?: LocalTextStyle.current,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon?.let {
            {
                Box(modifier = Modifier.testTag(FormTestTags.LEADING_ICON)) {
                    it()
                }
            }
        },
        trailingIcon = trailingIcon?.let {
            {
                Box(modifier = Modifier.testTag(FormTestTags.TRAILING_ICON)) {
                    it()
                }
            }
        },
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText?.let {
            {
                Box(modifier = Modifier.testTag(FormTestTags.SUPPORTING_TEXT)) {
                    it()
                }
            }
        },
        isError = isError,
        visualTransformation = config.visualTransformation,
        keyboardOptions = config.keyboardOptions,
        singleLine = config.isSingleLine,
        maxLines = config.maxLines,
        minLines = config.minLines,
        shape = config.shape,
        colors = colors
    )
}

/**
 * Error text component for displaying validation errors
 */
@Composable
fun ErrorText(
    errorMessage: String,
    modifier: Modifier = Modifier,
    colors: FormFieldColors = FormFieldColors.default(),
    textStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
) {
    Text(
        text = errorMessage,
        color = colors.errorTextColor,
        style = textStyle,
        modifier = modifier.testTag(FormTestTags.ERROR_TEXT)
    )
}

/**
 * Utility function to determine validation state from PulseField FormField
 */
@Composable
fun <T> FormField<T>.collectValidationState(): FieldValidationState {
    val fieldValue by fieldFlow.collectAsState()
    val isValid by isValid.collectAsState(initial = false)
    val errorMessage by error.collectAsState(initial = null)

    return when {
        fieldValue == null -> FieldValidationState.Pristine
        isValid -> FieldValidationState.Valid
        else -> FieldValidationState.Invalid(errorMessage ?: "Invalid input")
    }
}