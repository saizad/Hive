package com.hive.com.hive.exts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hive.exts.FormFieldConfig
import com.hive.exts.*
import com.pulse.field.FormField
import com.pulse.field.StringField
import com.pulse.field.EmailField
import com.pulse.field.IntField
import com.pulse.field.DoubleField
import com.pulse.field.PasswordField

/**
 * Preview for StringTextField with various states
 */
@Preview(name = "String Text Fields", showBackground = true)
@Composable
fun StringTextFieldPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("String Text Fields", style = MaterialTheme.typography.headlineSmall)

                // Basic text field
                val nameField = remember { StringField("fullName", isMandatory = false, ogField = "John Doe") }
                StringTextField(
                    field = nameField,
                    label = { Text("Full Name") },
                    placeholder = { Text("Enter your name") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Person") }
                )

                // Email field - using EmailField from PulseField
                val emailField = remember { EmailField("email", isMandatory = false, ogField = "john@example.com") }
                StringTextField(
                    field = emailField,
                    label = { Text("Email Address") },
                    placeholder = { Text("Enter your email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") }
                )

                // Error state field
                val errorField = remember {
                    StringField("required", isMandatory = true, ogField = "").apply {
                        updateField("") // This will trigger validation error
                    }
                }
                StringTextField(
                    field = errorField,
                    label = { Text("Required Field") },
                    placeholder = { Text("This field shows error state") }
                )

                // Disabled field
                val disabledField = remember { StringField("disabled", isMandatory = false, ogField = "Disabled content") }
                StringTextField(
                    field = disabledField,
                    label = { Text("Disabled Field") },
                    config = FormFieldConfig(isEnabled = false)
                )

                // Read-only field
                val readOnlyField = remember { StringField("readOnly", isMandatory = false, ogField = "Read-only content") }
                StringTextField(
                    field = readOnlyField,
                    label = { Text("Read-only Field") },
                    config = FormFieldConfig(isReadOnly = true)
                )
            }
        }
    }
}

/**
 * Preview for IntTextField
 */
@Preview(name = "Integer Text Fields", showBackground = true)
@Composable
fun IntTextFieldPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Integer Text Fields", style = MaterialTheme.typography.headlineSmall)

                // Age field - using IntField from PulseField
                val ageField = remember { IntField("age", isMandatory = false, ogField = 25) }
                IntTextField(
                    field = ageField,
                    label = { Text("Age") },
                    placeholder = { Text("Enter your age") }
                )

                // Quantity field with prefix/suffix
                val quantityField = remember { IntField("quantity", isMandatory = false, ogField = 10) }
                IntTextField(
                    field = quantityField,
                    label = { Text("Quantity") },
                    suffix = { Text("items") }
                )

                // Error state
                val errorIntField = remember {
                    IntField("requiredNumber", isMandatory = true, ogField = null).apply {
                        updateField(null) // This will trigger validation error
                    }
                }
                IntTextField(
                    field = errorIntField,
                    label = { Text("Required Number") },
                    placeholder = { Text("Enter a valid number") }
                )
            }
        }
    }
}

/**
 * Preview for DoubleTextField
 */
@Preview(name = "Double Text Fields", showBackground = true)
@Composable
fun DoubleTextFieldPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Double Text Fields", style = MaterialTheme.typography.headlineSmall)

                // Price field - using DoubleField from PulseField
                val priceField = remember { DoubleField("price", isMandatory = false, ogField = 99.99) }
                DoubleTextField(
                    field = priceField,
                    label = { Text("Price") },
                    prefix = { Text("₹") },
                    placeholder = { Text("0.00") }
                )

                // Height field
                val heightField = remember { DoubleField("height", isMandatory = false, ogField = 5.8) }
                DoubleTextField(
                    field = heightField,
                    label = { Text("Height") },
                    suffix = { Text("ft") },
                    placeholder = { Text("Enter height") }
                )

                // Error state
                val errorDoubleField = remember {
                    DoubleField("amount", isMandatory = true, ogField = null).apply {
                        updateField(null) // This will trigger validation error
                    }
                }
                DoubleTextField(
                    field = errorDoubleField,
                    label = { Text("Amount") },
                    prefix = { Text("₹") }
                )
            }
        }
    }
}

/**
 * Preview for DropdownField
 */
@Preview(name = "Dropdown Fields", showBackground = true)
@Composable
fun DropdownFieldPreview() {
    val countries = listOf("India", "United States", "United Kingdom", "Canada", "Australia")

    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Dropdown Fields", style = MaterialTheme.typography.headlineSmall)

                // Basic dropdown - using StringField for dropdown selection
                val countryField = remember { StringField("country", isMandatory = false, ogField = "India") }
                DropdownField(
                    field = countryField,
                    options = countries,
                    optionToString = { it },
                    label = { Text("Country") },
                    placeholder = { Text("Select a country") }
                )

                // Dropdown with no selection
                val preferredCountryField = remember { StringField("preferredCountry", isMandatory = false, ogField = null) }
                DropdownField(
                    field = preferredCountryField,
                    options = countries,
                    optionToString = { it },
                    label = { Text("Preferred Country") },
                    placeholder = { Text("Choose your preference") }
                )

                // Error state dropdown
                val requiredCountryField = remember {
                    StringField("requiredCountry", isMandatory = true, ogField = null).apply {
                        updateField(null) // This will trigger validation error
                    }
                }
                DropdownField(
                    field = requiredCountryField,
                    options = countries,
                    optionToString = { it },
                    label = { Text("Required Country") },
                    placeholder = { Text("Selection required") }
                )

                // Empty options dropdown
                val emptyOptionsField = remember { StringField("emptyOptions", isMandatory = false, ogField = null) }
                DropdownField(
                    field = emptyOptionsField,
                    options = emptyList(),
                    optionToString = { it },
                    label = { Text("Empty Options") },
                    placeholder = { Text("No options available") }
                )
            }
        }
    }
}

/**
 * Preview for SearchableDropdownField
 */
@Preview(name = "Searchable Dropdown Fields", showBackground = true)
@Composable
fun SearchableDropdownFieldPreview() {
    val cities = listOf(
        "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Ahmedabad", "Chennai",
        "Kolkata", "Surat", "Pune", "Jaipur", "Lucknow", "Kanpur"
    )

    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Searchable Dropdown Fields", style = MaterialTheme.typography.headlineSmall)

                // Basic searchable dropdown
                val cityField = remember { StringField("city", isMandatory = false, ogField = "Mumbai") }
                SearchableDropdownField(
                    field = cityField,
                    options = cities,
                    optionToString = { it },
                    label = { Text("City") },
                    placeholder = { Text("Select or search for a city") },
                    searchPlaceholder = { Text("Type to search cities...") }
                )

                // Searchable dropdown with no selection
                val destinationField = remember { StringField("destination", isMandatory = false, ogField = null) }
                SearchableDropdownField(
                    field = destinationField,
                    options = cities,
                    optionToString = { it },
                    label = { Text("Destination City") },
                    placeholder = { Text("Search and select city") }
                )

                // Error state
                val requiredCityField = remember {
                    StringField("requiredCity", isMandatory = true, ogField = null).apply {
                        updateField(null) // This will trigger validation error
                    }
                }
                SearchableDropdownField(
                    field = requiredCityField,
                    options = cities,
                    optionToString = { it },
                    label = { Text("Required City") },
                    placeholder = { Text("Must select a city") }
                )
            }
        }
    }
}

/**
 * Preview showing all field types together
 */
@Preview(name = "All Form Fields", showBackground = true, heightDp = 800)
@Composable
fun AllFormFieldsPreview() {
    val priorities = listOf("Low", "Medium", "High", "Critical")
    val countries = listOf("India", "USA", "UK", "Canada")

    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("Complete Form Preview", style = MaterialTheme.typography.headlineMedium)

                // Personal Information Section
                Text("Personal Information", style = MaterialTheme.typography.titleMedium)

                val fullNameField = remember { StringField("fullName", isMandatory = false, ogField = "John Doe") }
                StringTextField(
                    field = fullNameField,
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) }
                )

                val emailField = remember { EmailField("email", isMandatory = false, ogField = "john@example.com") }
                StringTextField(
                    field = emailField,
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) }
                )

                val phoneField = remember { StringField("phone", isMandatory = false, ogField = "+91 98765 43210") }
                StringTextField(
                    field = phoneField,
                    label = { Text("Phone") },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) }
                )

                val ageField = remember { IntField("age", isMandatory = false, ogField = 28) }
                IntTextField(
                    field = ageField,
                    label = { Text("Age") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Preferences Section
                Text("Preferences", style = MaterialTheme.typography.titleMedium)

                val countryField = remember { StringField("country", isMandatory = false, ogField = "India") }
                DropdownField(
                    field = countryField,
                    options = countries,
                    optionToString = { it },
                    label = { Text("Country") }
                )

                val priorityField = remember { StringField("priority", isMandatory = false, ogField = "High") }
                DropdownField(
                    field = priorityField,
                    options = priorities,
                    optionToString = { it },
                    label = { Text("Priority") }
                )

                val budgetField = remember { DoubleField("budget", isMandatory = false, ogField = 50000.0) }
                DoubleTextField(
                    field = budgetField,
                    label = { Text("Budget") },
                    prefix = { Text("₹") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Error States Section
                Text("Error States", style = MaterialTheme.typography.titleMedium)

                val requiredStringField = remember {
                    StringField("requiredField", isMandatory = true, ogField = "").apply {
                        updateField("")
                    }
                }
                StringTextField(
                    field = requiredStringField,
                    label = { Text("Required Field") },
                    placeholder = { Text("Please enter a value") }
                )

                val requiredIntField = remember {
                    IntField("quantity", isMandatory = true, ogField = null).apply {
                        updateField(null)
                    }
                }
                IntTextField(
                    field = requiredIntField,
                    label = { Text("Quantity") },
                    placeholder = { Text("Enter quantity") }
                )

                val requiredDropdownField = remember {
                    StringField("requiredPriority", isMandatory = true, ogField = null).apply {
                        updateField(null)
                    }
                }
                DropdownField(
                    field = requiredDropdownField,
                    options = priorities,
                    optionToString = { it },
                    label = { Text("Required Priority") },
                    placeholder = { Text("Select priority") }
                )
            }
        }
    }
}

/**
 * Preview for different field configurations
 */
@Preview(name = "Field Configurations", showBackground = true)
@Composable
fun FieldConfigurationsPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("Field Configurations", style = MaterialTheme.typography.headlineSmall)

                // Single line field
                val singleLineField = remember { StringField("singleLine", isMandatory = false, ogField = "Single line text") }
                StringTextField(
                    field = singleLineField,
                    label = { Text("Single Line") },
                    config = FormFieldConfig(isSingleLine = true)
                )

                // Multi-line field
                val multiLineField = remember { StringField("multiLine", isMandatory = false, ogField = "This is a\nmulti-line\ntext field") }
                StringTextField(
                    field = multiLineField,
                    label = { Text("Multi Line") },
                    config = FormFieldConfig(isSingleLine = false, minLines = 3, maxLines = 5)
                )

                // Disabled field
                val disabledField = remember { StringField("disabled", isMandatory = false, ogField = "Disabled content") }
                StringTextField(
                    field = disabledField,
                    label = { Text("Disabled") },
                    config = FormFieldConfig(isEnabled = false)
                )

                // Read-only field
                val readOnlyField = remember { StringField("readOnly", isMandatory = false, ogField = "Read-only content") }
                StringTextField(
                    field = readOnlyField,
                    label = { Text("Read Only") },
                    config = FormFieldConfig(isReadOnly = true)
                )

                // Field with custom colors (error theme)
                val customColorField = remember { StringField("customColor", isMandatory = false, ogField = "Custom colored field") }
                StringTextField(
                    field = customColorField,
                    label = { Text("Custom Colors") },
                    colors = FormFieldColors.default()
                )
            }
        }
    }
}

/**
 * Preview for custom error content
 */
@Preview(name = "Custom Error Content", showBackground = true)
@Composable
fun CustomErrorContentPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Custom Error Content", style = MaterialTheme.typography.headlineSmall)

                // Default error styling
                val defaultErrorField = remember {
                    StringField("defaultError", isMandatory = true, ogField = "").apply {
                        updateField("")
                    }
                }
                StringTextField(
                    field = defaultErrorField,
                    label = { Text("Default Error") },
                    placeholder = { Text("This shows default error styling") }
                )

                // Custom error content
                val customErrorField = remember {
                    StringField("customError", isMandatory = true, ogField = "").apply {
                        updateField("")
                    }
                }
                StringTextField(
                    field = customErrorField,
                    label = { Text("Custom Error") },
                    placeholder = { Text("This shows custom error styling") },
                    customErrorContent = { errorMessage ->
                        Text(
                            text = "⚠️ $errorMessage",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )

                // No error text shown
                val hiddenErrorField = remember {
                    StringField("hiddenError", isMandatory = true, ogField = "").apply {
                        updateField("")
                    }
                }
                StringTextField(
                    field = hiddenErrorField,
                    label = { Text("Hidden Error Text") },
                    placeholder = { Text("Error text is hidden") },
                    showErrorText = false
                )
            }
        }
    }
}

/**
 * Preview for dropdown with custom content
 */
@Preview(name = "Custom Dropdown Content", showBackground = true)
@Composable
fun CustomDropdownContentPreview() {
    data class User(val name: String, val role: String, val isActive: Boolean)

    val users = listOf(
        User("John Doe", "Admin", true),
        User("Jane Smith", "User", true),
        User("Bob Johnson", "Moderator", false),
        User("Alice Brown", "User", true)
    )

    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Custom Dropdown Content", style = MaterialTheme.typography.headlineSmall)

                // Dropdown with custom option rendering
                val userField = remember { FormField("user", isMandatory = false, ogField = users[0]) }
                DropdownField(
                    field = userField,
                    options = users,
                    optionToString = { "${it.name} (${it.role})" },
                    label = { Text("Select User") },
                    optionContent = { user ->
                        Column {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${user.role} • ${if (user.isActive) "Active" else "Inactive"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                // Searchable dropdown with custom search
                val searchUserField = remember { FormField<User?>("searchUser", isMandatory = false, ogField = null) }
                SearchableDropdownField(
                    field = searchUserField,
                    options = users,
                    optionToString = { it?.name ?: "" },
                    searchFilter = { user, query ->
                        user?.name?.contains(query, ignoreCase = true) == true ||
                                user?.role?.contains(query, ignoreCase = true) == true
                    },
                    label = { Text("Search Users") },
                    placeholder = { Text("Select or search for user") },
                    optionContent = { user ->
                        Column {
                            Text(
                                text = user?.name ?: "NA",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = user?.role ?: "NA",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * Preview for fields with icons and decorations
 */
@Preview(name = "Fields with Decorations", showBackground = true)
@Composable
fun FieldDecorationsPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("Fields with Decorations", style = MaterialTheme.typography.headlineSmall)

                // Field with leading icon
                val emailField = remember { EmailField("email", isMandatory = false, ogField = "john@example.com") }
                StringTextField(
                    field = emailField,
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") }
                )

                // Field with prefix and suffix
                val priceField = remember { DoubleField("price", isMandatory = false, ogField = 1500.0) }
                DoubleTextField(
                    field = priceField,
                    label = { Text("Price") },
                    prefix = { Text("₹") },
                    suffix = { Text(".00") }
                )

                // Field with leading icon and suffix
                val phoneField = remember { StringField("phone", isMandatory = false, ogField = "+91 98765 43210") }
                StringTextField(
                    field = phoneField,
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = "Phone") },
                    suffix = { Text("IN") }
                )

                // Password field with leading icon
                val passwordField = remember { PasswordField("password", isMandatory = false, ogField = "password123") }
                StringTextField(
                    field = passwordField,
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
                    config = FormFieldConfig(
                        visualTransformation = PasswordVisualTransformation()
                    )
                )

                // Currency field with all decorations
                val salaryField = remember { DoubleField("salary", isMandatory = false, ogField = 25000.50) }
                DoubleTextField(
                    field = salaryField,
                    label = { Text("Annual Salary") },
                    leadingIcon = {
                        Text(
                            "💰",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    prefix = { Text("₹") },
                    suffix = { Text("per year") }
                )
            }
        }
    }
}