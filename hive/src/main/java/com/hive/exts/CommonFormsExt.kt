package com.hive.exts

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.hive.exts.StringTextField
import com.hive.exts.DoubleTextField
import com.hive.exts.DropdownField
import com.hive.exts.FormFieldColors
import com.hive.exts.FormFieldConfig
import com.hive.exts.IntTextField
import com.hive.exts.SearchableDropdownField
import com.pulse.field.FormField

/**
 * Extension functions and builders for common form field configurations
 * 
 * This file provides:
 * - Convenient extension functions for PulseField FormFields
 * - Pre-configured field types for common use cases
 * - Builder pattern for complex field configurations
 * - Commonly used keyboard options and visual transformations
 */

/**
 * Common keyboard configurations
 */
object KeyboardConfigs {
    val Email = KeyboardOptions(
        keyboardType = KeyboardType.Email,
        capitalization = KeyboardCapitalization.None
    )
    
    val Password = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        capitalization = KeyboardCapitalization.None
    )
    
    val Phone = KeyboardOptions(
        keyboardType = KeyboardType.Phone
    )
    
    val Number = KeyboardOptions(
        keyboardType = KeyboardType.Number
    )
    
    val Decimal = KeyboardOptions(
        keyboardType = KeyboardType.Decimal
    )
    
    val PersonName = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Words
    )
    
    val Sentence = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Sentences
    )
    
    val Uri = KeyboardOptions(
        keyboardType = KeyboardType.Uri,
        capitalization = KeyboardCapitalization.None
    )
}

/**
 * Builder class for creating FormFieldConfig with fluent API
 */
class FormFieldConfigBuilder {
    private var isEnabled: Boolean = true
    private var isReadOnly: Boolean = false
    private var isSingleLine: Boolean = false
    private var maxLines: Int = Int.MAX_VALUE
    private var minLines: Int = 1
    private var keyboardOptions: KeyboardOptions = KeyboardOptions.Default
    private var visualTransformation: VisualTransformation = VisualTransformation.None
    private var shape: Shape? = null
    private var colors: FormFieldColors? = null
    private var textStyle: TextStyle? = null
    
    fun enabled(enabled: Boolean) = apply { isEnabled = enabled }
    fun readOnly(readOnly: Boolean = true) = apply { isReadOnly = readOnly }
    fun singleLine(singleLine: Boolean = true) = apply { 
        isSingleLine = singleLine 
        if (singleLine) maxLines = 1
    }
    fun maxLines(lines: Int) = apply { maxLines = lines }
    fun minLines(lines: Int) = apply { minLines = lines }
    fun keyboardOptions(options: KeyboardOptions) = apply { keyboardOptions = options }
    fun visualTransformation(transformation: VisualTransformation) = apply { visualTransformation = transformation }
    fun shape(fieldShape: Shape) = apply { shape = fieldShape }
    fun colors(fieldColors: FormFieldColors) = apply { colors = fieldColors }
    fun textStyle(style: TextStyle) = apply { textStyle = style }
    
    @Composable
    fun build(): FormFieldConfig {
        return FormFieldConfig(
            isEnabled = isEnabled,
            isReadOnly = isReadOnly,
            isSingleLine = isSingleLine,
            maxLines = maxLines,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            shape = shape ?: MaterialTheme.shapes.large,
            colors = colors,
            textStyle = textStyle
        )
    }
}

/**
 * DSL function for building FormFieldConfig
 */
@Composable
fun fieldConfig(block: FormFieldConfigBuilder.() -> Unit): FormFieldConfig {
    return FormFieldConfigBuilder().apply(block).build()
}

/**
 * Extension functions for common form field types
 */

/**
 * Extension function for email text fields
 */
@Composable
fun FormField<String>.asEmailField(
    modifier: Modifier = Modifier,
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    StringTextField(
        field = this,
        modifier = modifier,
        config = fieldConfig {
            keyboardOptions(KeyboardConfigs.Email)
            singleLine()
        },
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label
    )
}

/**
 * Extension function for password text fields
 */
@Composable
fun FormField<String>.asPasswordField(
    modifier: Modifier = Modifier,
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    showPassword: Boolean = false
) {
    StringTextField(
        field = this,
        modifier = modifier,
        config = fieldConfig {
            keyboardOptions(KeyboardConfigs.Password)
            singleLine()
            visualTransformation(
                if (showPassword) VisualTransformation.None 
                else PasswordVisualTransformation()
            )
        },
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label
    )
}

/**
 * Extension function for phone number text fields
 */
@Composable
fun FormField<String>.asPhoneField(
    modifier: Modifier = Modifier,
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    StringTextField(
        field = this,
        modifier = modifier,
        config = fieldConfig {
            keyboardOptions(KeyboardConfigs.Phone)
            singleLine()
        },
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label
    )
}

/**
 * Extension function for person name text fields
 */
@Composable
fun FormField<String>.asPersonNameField(
    modifier: Modifier = Modifier,
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    StringTextField(
        field = this,
        modifier = modifier,
        config = fieldConfig {
            keyboardOptions(KeyboardConfigs.PersonName)
            singleLine()
        },
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label
    )
}

/**
 * Extension function for multiline text fields
 */
@Composable
fun FormField<String>.asMultilineField(
    modifier: Modifier = Modifier,
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    maxLines: Int = 5,
    minLines: Int = 3
) {
    StringTextField(
        field = this,
        modifier = modifier,
        config = fieldConfig {
            keyboardOptions(KeyboardConfigs.Sentence)
            maxLines(maxLines)
            minLines(minLines)
        },
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label
    )
}

/**
 * Extension function for currency amount fields
 */
@Composable
fun FormField<Double>.asCurrencyField(
    modifier: Modifier = Modifier,
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    currencySymbol: String = "₹"
) {
    DoubleTextField(
        field = this,
        modifier = modifier,
        config = fieldConfig {
            keyboardOptions(KeyboardConfigs.Decimal)
            singleLine()
        },
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label,
        prefix = { Text(currencySymbol) }
    )
}

/**
 * Extension function for age/number fields
 */
@Composable
fun FormField<Int>.asAgeField(
    modifier: Modifier = Modifier,
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    IntTextField(
        field = this,
        modifier = modifier,
        config = fieldConfig {
            keyboardOptions(KeyboardConfigs.Number)
            singleLine()
        },
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label
    )
}

/**
 * Extension function for simple dropdown fields
 */
@Composable
fun <T> FormField<T>.asDropdownField(
    modifier: Modifier = Modifier,
    options: List<T>,
    optionToString: (T) -> String,
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    DropdownField(
        field = this,
        options = options,
        optionToString = optionToString,
        modifier = modifier,
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        label = label
    )
}

/**
 * Extension function for searchable dropdown fields
 */
@Composable
fun <T> FormField<T>.asSearchableDropdownField(
    modifier: Modifier = Modifier,
    options: List<T>,
    optionToString: (T) -> String,
    searchFilter: (T, String) -> Boolean = { option, query ->
        optionToString(option).contains(query, ignoreCase = true)
    },
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    searchPlaceholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    SearchableDropdownField(
        field = this,
        options = options,
        optionToString = optionToString,
        searchFilter = searchFilter,
        modifier = modifier,
        colors = colors,
        placeholder = placeholder,
        searchPlaceholder = searchPlaceholder,
        leadingIcon = leadingIcon,
        label = label
    )
}

/**
 * Predefined common option lists
 */
object CommonOptions {
    val IndianStates = listOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
        "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
        "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
        "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
        "Uttar Pradesh", "Uttarakhand", "West Bengal"
    )
    
    val Genders = listOf("Male", "Female", "Other", "Prefer not to say")
    
    val MaritalStatus = listOf("Single", "Married", "Divorced", "Widowed")
    
    val BloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    
    val EducationLevels = listOf(
        "Below 10th", "10th Pass", "12th Pass", "Diploma", "Graduate", 
        "Post Graduate", "Doctorate", "Other"
    )
    
    val Priorities = listOf("Low", "Medium", "High", "Critical")
    
    val YesNo = listOf("Yes", "No")
}

/**
 * Helper function for creating option pairs (value to display string mapping)
 */
data class OptionPair<T>(val value: T, val display: String)

/**
 * Extension function to create dropdown from option pairs
 */
@Composable
fun <T> FormField<T>.asDropdownFieldWithPairs(
    modifier: Modifier = Modifier,
    options: List<OptionPair<T>>,
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    DropdownField(
        field = this,
        options = options.map { it.value },
        optionToString = { value -> 
            options.find { it.value == value }?.display ?: value.toString()
        },
        modifier = modifier,
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        label = label
    )
}