package com.hive.exts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulse.field.FormField



/**
 * Base reactive field component that handles validation and state management
 * Separates the form logic from UI rendering
 */
@Composable
fun <T> ReactiveFieldBase(
    field: FormField<T>,
    converter: FieldValueConverter<T>,
    modifier: Modifier = Modifier,
    content: @Composable (
        value: TextFieldValue,
        validationState: FieldValidationState,
        onValueChange: (TextFieldValue) -> Unit
    ) -> Unit
) {
    val fieldValue by field.fieldFlow.collectAsState()
    val validationState = field.collectValidationState()

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(converter.valueToString(fieldValue)))
    }

    // Sync text field with field value changes (e.g., programmatic updates)
    LaunchedEffect(fieldValue) {
        val newText = converter.valueToString(fieldValue)
        if (newText != textFieldValue.text) {
            val cursorPosition = minOf(newText.length, textFieldValue.selection.start)
            textFieldValue = TextFieldValue(
                text = newText,
                selection = TextRange(cursorPosition)
            )
        }
    }

    content(
         textFieldValue,
        validationState,
         {
            // Validate string format before updating
            if (converter.isValidString(it.text)) {
                textFieldValue = it
                field.updateField(converter.stringToValue(it.text))
            }
        }
    )
}

/**
 * Reactive text field component that integrates with PulseField validation
 *
 * This is the main text input component that provides:
 * - Real-time validation state updates
 * - Type-safe value conversion
 * - Consistent error handling and display
 * - Automatic cursor management
 * - Customizable appearance and behavior
 *
 * @param field The PulseField FormField instance for validation and state management
 * @param converter Value converter for type-safe string conversion
 * @param modifier Modifier for customization
 * @param config Configuration for field behavior and appearance
 * @param colors Color configuration (defaults to Material 3 colors)
 * @param placeholder Placeholder composable
 * @param leadingIcon Leading icon composable
 * @param trailingIcon Trailing icon composable
 * @param label Label composable
 * @param prefix Prefix composable
 * @param suffix Suffix composable
 * @param showErrorText Whether to show error text below the field
 * @param customErrorContent Custom error content composable
 */
@Composable
fun <T> ReactiveTextField(
    field: FormField<T>,
    converter: FieldValueConverter<T>,
    modifier: Modifier = Modifier,
    config: FormFieldConfig = FormFieldConfig(),
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    showErrorText: Boolean = true,
    customErrorContent: @Composable ((String) -> Unit)? = null
) {

    ReactiveFieldBase(
        field = field,
        converter = converter,
        modifier = modifier
    ) { textFieldValue, validationState, onValueChange ->

        val isError = validationState is FieldValidationState.Invalid
        val textFieldColors = colors.toTextFieldColors(validationState)

        BaseOutlinedTextField(
            value = textFieldValue,
            onValueChange = onValueChange,
            config = config,
            colors = textFieldColors,
            isError = isError,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            label = label,
            prefix = prefix,
            suffix = suffix,
            supportingText = if (showErrorText && isError) {
                {
                    val errorMessage = (validationState as FieldValidationState.Invalid).errorMessage
                    if (customErrorContent != null) {
                        customErrorContent(errorMessage)
                    } else {
                        ErrorText(errorMessage, colors = colors)
                    }
                }
            } else null
        )
    }
}

/**
 * Convenience composable for string fields
 */
@Composable
fun StringTextField(
    field: FormField<String>,
    modifier: Modifier = Modifier,
    config: FormFieldConfig = FormFieldConfig(),
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    showErrorText: Boolean = true,
    customErrorContent: @Composable ((String) -> Unit)? = null
) {
    ReactiveTextField(
        field = field,
        converter = FieldConverters.String,
        modifier = modifier,
        config = config,
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label,
        prefix = prefix,
        suffix = suffix,
        showErrorText = showErrorText,
        customErrorContent = customErrorContent
    )
}

/**
 * Convenience composable for integer fields
 */
@Composable
fun IntTextField(
    field: FormField<Int>,
    modifier: Modifier = Modifier,
    config: FormFieldConfig = FormFieldConfig(),
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    showErrorText: Boolean = true,
    customErrorContent: @Composable ((String) -> Unit)? = null
) {
    ReactiveTextField(
        field = field,
        converter = FieldConverters.Int,
        modifier = modifier,
        config = config,
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label,
        prefix = prefix,
        suffix = suffix,
        showErrorText = showErrorText,
        customErrorContent = customErrorContent
    )
}

/**
 * Convenience composable for double fields (useful for prices, amounts)
 */
@Composable
fun DoubleTextField(
    field: FormField<Double>,
    modifier: Modifier = Modifier,
    config: FormFieldConfig = FormFieldConfig(),
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    showErrorText: Boolean = true,
    customErrorContent: @Composable ((String) -> Unit)? = null
) {
    ReactiveTextField(
        field = field,
        converter = FieldConverters.Double,
        modifier = modifier,
        config = config,
        colors = colors,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label,
        prefix = prefix,
        suffix = suffix,
        showErrorText = showErrorText,
        customErrorContent = customErrorContent
    )
}

/**
 * Dropdown selection field component
 *
 * Provides a dropdown menu for selecting from a list of options with:
 * - Type-safe option handling
 * - Custom option rendering
 * - Search functionality (optional)
 * - Integration with PulseField validation
 * - Consistent Material 3 styling
 *
 * @param field The PulseField FormField for the selected value
 * @param options List of available options
 * @param optionToString Function to convert option to display string
 * @param modifier Modifier for customization
 * @param config Configuration for field behavior and appearance
 * @param colors Color configuration
 * @param placeholder Placeholder when no option is selected
 * @param leadingIcon Leading icon composable
 * @param trailingIcon Custom trailing icon (defaults to dropdown arrow)
 * @param label Label composable
 * @param showErrorText Whether to show error text below the field
 * @param customErrorContent Custom error content composable
 * @param optionContent Custom composable for rendering each option
 * @param emptyOptionsContent Content to show when no options available
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownField(
    field: FormField<T>,
    options: List<T>,
    optionToString: (T) -> String,
    modifier: Modifier = Modifier,
    config: FormFieldConfig = FormFieldConfig(),
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable ((Boolean) -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    showErrorText: Boolean = true,
    customErrorContent: @Composable ((String) -> Unit)? = null,
    optionContent: @Composable ((T) -> Unit)? = null,
    emptyOptionsContent: @Composable (() -> Unit)? = null
) {
    val fieldValue by field.fieldFlow.collectAsState()
    val validationState = field.collectValidationState()
    var expanded by remember { mutableStateOf(false) }

    val isError = validationState is FieldValidationState.Invalid
    val textFieldColors = colors.toTextFieldColors(validationState)

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (config.isEnabled && !config.isReadOnly) expanded = it },
            modifier = Modifier.testTag(FormTestTags.DROPDOWN_FIELD)
        ) {
            BaseOutlinedTextField(
                value = TextFieldValue(fieldValue?.let(optionToString) ?: ""),
                onValueChange = { /* Read-only field */ },
                config = config.copy(isReadOnly = true),
                colors = textFieldColors,
                isError = isError,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = {
                    if (trailingIcon != null) {
                        trailingIcon(expanded)
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                label = label,
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                    .clickable(enabled = config.isEnabled && !config.isReadOnly) {
                        expanded = !expanded
                    }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (options.isEmpty()) {
                    if (emptyOptionsContent != null) {
                        emptyOptionsContent()
                    } else {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text("No options available")
                        }
                    }
                } else {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                if (optionContent != null) {
                                    optionContent(option)
                                } else {
                                    Text(optionToString(option))
                                }
                            },
                            onClick = {
                                field.updateField(option)
                                expanded = false
                            },
                            modifier = Modifier.testTag("${FormTestTags.DROPDOWN_FIELD}_item_${optionToString(option)}")
                        )
                    }
                }
            }
        }

        // Error text
        if (showErrorText && isError) {
            val errorMessage = (validationState as FieldValidationState.Invalid).errorMessage
            if (customErrorContent != null) {
                customErrorContent(errorMessage)
            } else {
                ErrorText(errorMessage, colors = colors)
            }
        }
    }
}

/**
 * Searchable dropdown field with filtering capabilities
 *
 * Extends DropdownField with search functionality:
 * - Real-time filtering as user types
 * - Customizable search logic
 * - Option highlighting
 * - Clear search functionality
 *
 * @param field The PulseField FormField for the selected value
 * @param options List of available options
 * @param optionToString Function to convert option to display string
 * @param searchFilter Function to filter options based on search text
 * @param modifier Modifier for customization
 * @param config Configuration for field behavior and appearance
 * @param colors Color configuration
 * @param placeholder Placeholder when no option is selected
 * @param searchPlaceholder Placeholder for search input
 * @param leadingIcon Leading icon composable
 * @param label Label composable
 * @param showErrorText Whether to show error text below the field
 * @param customErrorContent Custom error content composable
 * @param optionContent Custom composable for rendering each option
 * @param emptySearchContent Content to show when search returns no results
 * @param maxDisplayOptions Maximum number of options to display in dropdown
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropdownField(
    modifier: Modifier = Modifier,
    field: FormField<T>,
    options: List<T>,
    optionToString: (T) -> String,
    searchFilter: (T, String) -> Boolean = { option, query ->
        optionToString(option).contains(query, ignoreCase = true)
    },
    config: FormFieldConfig = FormFieldConfig(),
    colors: FormFieldColors = FormFieldColors.default(),
    placeholder: @Composable (() -> Unit)? = null,
    searchPlaceholder: @Composable (() -> Unit)? = { Text("Search...") },
    leadingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    showErrorText: Boolean = true,
    customErrorContent: @Composable ((String) -> Unit)? = null,
    optionContent: @Composable ((T) -> Unit)? = null,
    emptySearchContent: @Composable (() -> Unit)? = null,
    maxDisplayOptions: Int = 10
) {
    val fieldValue by field.fieldFlow.collectAsState()
    val validationState = field.collectValidationState()
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val isError = validationState is FieldValidationState.Invalid
    val textFieldColors = colors.toTextFieldColors(validationState)

    // Filter options based on search query
    val filteredOptions = remember(options, searchQuery) {
        if (searchQuery.isEmpty()) {
            options.take(maxDisplayOptions)
        } else {
            options.filter { searchFilter(it, searchQuery) }.take(maxDisplayOptions)
        }
    }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (config.isEnabled && !config.isReadOnly) expanded = it },
            modifier = Modifier.testTag(FormTestTags.SEARCH_DROPDOWN_FIELD)
        ) {
            BaseOutlinedTextField(
                value = TextFieldValue(
                    if (expanded) searchQuery else (fieldValue?.let(optionToString) ?: "")
                ),
                onValueChange = { newValue ->
                    if (expanded) {
                        searchQuery = newValue.text
                    }
                },
                config = config.copy(isReadOnly = !expanded),
                colors = textFieldColors,
                isError = isError,
                placeholder = if (expanded) searchPlaceholder else placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                label = label,
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryEditable)
                    .clickable(enabled = config.isEnabled && !config.isReadOnly) {
                        if (!expanded) {
                            searchQuery = ""
                            expanded = true
                        }
                    }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    searchQuery = ""
                }
            ) {
                if (filteredOptions.isEmpty()) {
                    if (emptySearchContent != null) {
                        emptySearchContent()
                    } else {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(if (searchQuery.isEmpty()) "No options available" else "No results found")
                        }
                    }
                } else {
                    filteredOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                if (optionContent != null) {
                                    optionContent(option)
                                } else {
                                    Text(optionToString(option))
                                }
                            },
                            onClick = {
                                field.updateField(option)
                                expanded = false
                                searchQuery = ""
                            },
                            modifier = Modifier.testTag("${FormTestTags.SEARCH_DROPDOWN_FIELD}_item_${optionToString(option)}")
                        )
                    }
                }
            }
        }

        // Error text
        if (showErrorText && isError) {
            val errorMessage = (validationState as FieldValidationState.Invalid).errorMessage
            if (customErrorContent != null) {
                customErrorContent(errorMessage)
            } else {
                ErrorText(errorMessage, colors = colors)
            }
        }
    }
}