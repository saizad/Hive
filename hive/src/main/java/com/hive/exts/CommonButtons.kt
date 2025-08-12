package com.hive.exts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object CommonButtonDefaults {
    const val TAG_GRADIENT_BUTTON = "tag_gradient_button"
    const val TAG_GRADIENT_BUTTON_TEXT = "tag_gradient_button_text"

    // Common styles for consistent usage across app
    val commonTextStyle: TextStyle
        @Composable get() = MaterialTheme.typography.titleMedium.copy(
            fontSize = 16.sp,
            lineHeight = 14.sp,
            color = MaterialTheme.colorScheme.surface
        )

    val commonGradientTextStyle: TextStyle
        @Composable get() = MaterialTheme.typography.titleSmall.copy(
            fontSize = 16.sp,
            lineHeight = 14.sp,
        )

    val commonShape: Shape @Composable get() = MaterialTheme.shapes.extraLarge
    val commonGradientShape: Shape @Composable get() = MaterialTheme.shapes.large
    val commonPadding: PaddingValues @Composable get() = PaddingValues(12.dp)
    val commonHeight: Dp @Composable get() = 56.dp

    val defaultGradientColors: List<Color>
        @Composable get() = listOf(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.primary,
        )

    val defaultDisabledGradientColors: List<Color>
        @Composable get() = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
        )
}

// ============================================
// DEPRECATED FUNCTIONS - KEPT FOR MIGRATION
// ============================================

/**
 * @deprecated Use Material 3 IconButton directly with your own styling
 *
 * Replace with:
 * ```
 * IconButton(
 *     onClick = onClick,
 *     modifier = modifier
 *         .size(40.dp)
 *         .background(
 *             color = MaterialTheme.colorScheme.onSurface.copy(0.035f),
 *             shape = MaterialTheme.shapes.large
 *         )
 * ) {
 *     Icon(
 *         imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
 *         contentDescription = "back",
 *         tint = MaterialTheme.colorScheme.primary,
 *     )
 * }
 * ```
 */
@Deprecated(
    message = "Use Material 3 IconButton directly. This wrapper adds no meaningful value.",
    replaceWith = ReplaceWith(
        "IconButton(onClick = onClick, modifier = modifier.size(40.dp).background(MaterialTheme.colorScheme.onSurface.copy(0.035f), MaterialTheme.shapes.large)) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, \"back\", tint = MaterialTheme.colorScheme.primary) }",
        "androidx.compose.material3.IconButton",
        "androidx.compose.material3.Icon",
        "androidx.compose.material.icons.Icons",
        "androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft",
        "androidx.compose.foundation.background",
        "androidx.compose.foundation.layout.size"
    ),
    level = DeprecationLevel.WARNING
)
@Composable
fun NavigationBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(0.035f),
                shape = MaterialTheme.shapes.large
            )
            .testTag("NavigationBackButton"),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "back",
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

/**
 * @deprecated Use Material 3 Button directly with ButtonDefaults for consistency
 *
 * Replace with:
 * ```
 * Button(
 *     onClick = onClick,
 *     modifier = modifier.commonButtonStyle(),
 *     enabled = isEnabled,
 *     contentPadding = paddingValues,
 *     colors = ButtonDefaults.buttonColors(
 *         containerColor = enabledColor,
 *         disabledContainerColor = disabledColor
 *     ),
 *     shape = shape
 * ) {
 *     text()
 * }
 * ```
 *
 * Or use AppButton() for text-based buttons with design system defaults.
 */
@Deprecated(
    message = "Use Material 3 Button directly or AppButton() for text buttons. This wrapper adds minimal value.",
    replaceWith = ReplaceWith(
        "Button(onClick = onClick, modifier = modifier.commonButtonStyle(), enabled = isEnabled, contentPadding = paddingValues, colors = ButtonDefaults.buttonColors(containerColor = enabledColor, disabledContainerColor = disabledColor), shape = shape) { text() }",
        "androidx.compose.material3.Button",
        "androidx.compose.material3.ButtonDefaults"
    ),
    level = DeprecationLevel.WARNING
)
@Composable
fun CommonButton(
    modifier: Modifier = Modifier,
    enabledColor: Color = MaterialTheme.colorScheme.primary,
    disabledColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
    paddingValues: PaddingValues = CommonButtonDefaults.commonPadding,
    shape: Shape = CommonButtonDefaults.commonShape,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
    text: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .commonButtonStyle()
            .testTag("common_button_tag"),
        enabled = isEnabled,
        contentPadding = paddingValues,
        colors = ButtonDefaults.buttonColors(
            containerColor = enabledColor,
            disabledContainerColor = disabledColor
        ),
        shape = shape,
    ) {
        text()
    }
}

/**
 * @deprecated Use AppButton() instead for better design system integration
 *
 * Replace with:
 * ```
 * AppButton(
 *     text = text,
 *     onClick = onClick,
 *     modifier = modifier,
 *     enabled = isEnabled,
 *     textStyle = textStyle,
 *     // ... other parameters
 * )
 * ```
 */
@Deprecated(
    message = "Use AppButton() for better design system integration and consistency.",
    replaceWith = ReplaceWith(
        "AppButton(text = text, onClick = onClick, modifier = modifier, enabled = isEnabled, textStyle = textStyle)",
        "com.nursing.next.live.ui.utils.AppButton"
    ),
    level = DeprecationLevel.WARNING
)
@Composable
fun CommonButton(
    modifier: Modifier = Modifier,
    enabledColor: Color = MaterialTheme.colorScheme.primary,
    disabledColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
    text: String,
    textStyle: TextStyle = CommonButtonDefaults.commonTextStyle,
    paddingValues: PaddingValues = CommonButtonDefaults.commonPadding,
    shape: Shape = CommonButtonDefaults.commonShape,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .commonButtonStyle()
            .testTag("common_button_text_tag"),
        enabled = isEnabled,
        contentPadding = paddingValues,
        colors = ButtonDefaults.buttonColors(
            containerColor = enabledColor,
            disabledContainerColor = disabledColor
        ),
        shape = shape,
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}


/**
 * Super extensible gradient button that can handle any gradient configuration
 * This is the ONLY button component that adds real value over Material 3
 */
@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // Gradient Configuration - Maximum Flexibility
    backgroundBrush: Brush? = null, // Direct brush control
    colors: List<Color> = CommonButtonDefaults.defaultGradientColors,
    disabledColors: List<Color> = CommonButtonDefaults.defaultDisabledGradientColors,
    gradientDirection: GradientDirection = GradientDirection.LeftToRight,

    // Appearance
    shape: Shape = CommonButtonDefaults.commonGradientShape,
    height: Dp = CommonButtonDefaults.commonHeight,
    width: Modifier = Modifier.fillMaxWidth(),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,

    // Content & Padding
    contentPadding: PaddingValues = CommonButtonDefaults.commonPadding,
    contentAlignment: Alignment = Alignment.Center,

    // State
    enabled: Boolean = true,

    // Testing
    testTag: String = CommonButtonDefaults.TAG_GRADIENT_BUTTON,

    // Content
    content: @Composable () -> Unit
) {
    val finalBrush = backgroundBrush ?: when (gradientDirection) {
        GradientDirection.LeftToRight -> Brush.horizontalGradient(if (enabled) colors else disabledColors)
        GradientDirection.TopToBottom -> Brush.verticalGradient(if (enabled) colors else disabledColors)
        GradientDirection.Diagonal -> Brush.linearGradient(if (enabled) colors else disabledColors)
        GradientDirection.Radial -> Brush.radialGradient(if (enabled) colors else disabledColors)
        GradientDirection.Sweep -> Brush.sweepGradient(if (enabled) colors else disabledColors)
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .then(width)
            .testTag(testTag),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp), // We handle padding in the Box
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = shape,
        border = border,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .background(
                    brush = finalBrush,
                    shape = shape
                )
                .padding(contentPadding),
            contentAlignment = contentAlignment,
        ) {
            content()
        }
    }
}

/**
 * Convenience overload for simple text-based gradient buttons
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = CommonButtonDefaults.commonGradientTextStyle,
    textColor: Color = MaterialTheme.colorScheme.surface,
    textTestTag: String = CommonButtonDefaults.TAG_GRADIENT_BUTTON_TEXT,

    // All the same extensibility options
    backgroundBrush: Brush? = null,
    colors: List<Color> = CommonButtonDefaults.defaultGradientColors,
    disabledColors: List<Color> = CommonButtonDefaults.defaultDisabledGradientColors,
    gradientDirection: GradientDirection = GradientDirection.LeftToRight,
    shape: Shape = CommonButtonDefaults.commonGradientShape,
    height: Dp = CommonButtonDefaults.commonHeight,
    width: Modifier = Modifier.fillMaxWidth(),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    contentPadding: PaddingValues = CommonButtonDefaults.commonPadding,
    contentAlignment: Alignment = Alignment.Center,
    enabled: Boolean = true,
    testTag: String = CommonButtonDefaults.TAG_GRADIENT_BUTTON,
) {
    GradientButton(
        onClick = onClick,
        modifier = modifier,
        backgroundBrush = backgroundBrush,
        colors = colors,
        disabledColors = disabledColors,
        gradientDirection = gradientDirection,
        shape = shape,
        height = height,
        width = width,
        border = border,
        elevation = elevation,
        contentPadding = contentPadding,
        contentAlignment = contentAlignment,
        enabled = enabled,
        testTag = testTag
    ) {
        Text(
            text = text,
            modifier = Modifier.testTag(textTestTag),
            color = textColor,
            style = textStyle
        )
    }
}

/**
 * Gradient direction options for maximum flexibility
 */
enum class GradientDirection {
    LeftToRight,
    TopToBottom,
    Diagonal,
    Radial,
    Sweep
}

// Extension functions for common Material 3 button configurations using your design system
@Composable
fun Modifier.commonButtonStyle() = this
    .height(CommonButtonDefaults.commonHeight)
    .fillMaxWidth()

/**
 * Pre-configured Material 3 button with your design system defaults
 * Use this instead of the old CommonButton wrapper
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    ),
    textStyle: TextStyle = CommonButtonDefaults.commonTextStyle,
    shape: Shape = CommonButtonDefaults.commonShape,
    contentPadding: PaddingValues = CommonButtonDefaults.commonPadding,
    border: BorderStroke? = null,
    testTag: String? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .commonButtonStyle()
            .let { if (testTag != null) it.testTag(testTag) else it },
        enabled = enabled,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        border = border
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

// ============================================
// COMPREHENSIVE PREVIEWS FOR ALL VARIANTS
// ============================================

@Preview(name = "Gradient Buttons - All Directions", showBackground = true)
@Composable
private fun GradientButtonDirectionsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Gradient Directions", style = MaterialTheme.typography.titleMedium)

            GradientButton(
                text = "Left to Right",
                onClick = { },
                gradientDirection = GradientDirection.LeftToRight
            )

            GradientButton(
                text = "Top to Bottom",
                onClick = { },
                gradientDirection = GradientDirection.TopToBottom
            )

            GradientButton(
                text = "Diagonal",
                onClick = { },
                gradientDirection = GradientDirection.Diagonal
            )

            GradientButton(
                text = "Radial",
                onClick = { },
                gradientDirection = GradientDirection.Radial
            )

            GradientButton(
                text = "Sweep",
                onClick = { },
                gradientDirection = GradientDirection.Sweep
            )
        }
    }
}

@Preview(name = "Gradient Button States", showBackground = true)
@Composable
private fun GradientButtonStatesPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Button States", style = MaterialTheme.typography.titleMedium)

            GradientButton(
                text = "Enabled Button",
                onClick = { },
                enabled = true
            )

            GradientButton(
                text = "Disabled Button",
                onClick = { },
                enabled = false
            )

            // Custom colors
            GradientButton(
                text = "Custom Colors",
                onClick = { },
                colors = listOf(Color.Red, Color.Magenta, Color.Yellow)
            )
        }
    }
}

@Preview(name = "Gradient Button Sizes & Shapes", showBackground = true)
@Composable
private fun GradientButtonSizesPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Sizes & Shapes", style = MaterialTheme.typography.titleMedium)

            GradientButton(
                text = "Small Button",
                onClick = { },
                height = 40.dp
            )

            GradientButton(
                text = "Large Button",
                onClick = { },
                height = 64.dp
            )

            GradientButton(
                text = "Rounded Rectangle",
                onClick = { },
                shape = RoundedCornerShape(8.dp)
            )

            GradientButton(
                text = "Circular Ends",
                onClick = { },
                shape = RoundedCornerShape(50)
            )

            // Half width button
            GradientButton(
                text = "Half Width",
                onClick = { },
                width = Modifier.fillMaxWidth(0.5f)
            )
        }
    }
}

@Preview(name = "Gradient Button with Borders & Elevation", showBackground = true)
@Composable
private fun GradientButtonBordersPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Borders & Elevation", style = MaterialTheme.typography.titleMedium)

            GradientButton(
                text = "With Border",
                onClick = { },
                border = BorderStroke(2.dp, Color.Black)
            )

            GradientButton(
                text = "With Elevation",
                onClick = { },
                elevation = 8.dp
            )

            GradientButton(
                text = "Border + Elevation",
                onClick = { },
                border = BorderStroke(1.dp, Color.Gray),
                elevation = 4.dp
            )
        }
    }
}

@Preview(name = "Custom Gradient Button Content", showBackground = true)
@Composable
private fun GradientButtonCustomContentPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Custom Content", style = MaterialTheme.typography.titleMedium)

            GradientButton(
                onClick = { }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text("Play Video", color = Color.White)
                }
            }

            GradientButton(
                onClick = { },
                contentAlignment = Alignment.CenterStart,
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                )
            ) {
                Text("Left Aligned", color = Color.White)
            }

            GradientButton(
                onClick = { },
                height = 80.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text("Upload", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(name = "AppButton Variants", showBackground = true)
@Composable
private fun AppButtonPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("AppButton Variants", style = MaterialTheme.typography.titleMedium)

            AppButton(
                text = "Primary Button",
                onClick = { }
            )

            AppButton(
                text = "Disabled Button",
                onClick = { },
                enabled = false
            )

            AppButton(
                text = "Secondary Button",
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )

            AppButton(
                text = "With Border",
                onClick = { },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Preview(name = "Custom Brush Examples", showBackground = true)
@Composable
private fun CustomBrushPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Custom Brushes", style = MaterialTheme.typography.titleMedium)

            GradientButton(
                text = "Complex Linear",
                onClick = { },
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFFf093fb)
                    )
                )
            )

            GradientButton(
                text = "Rainbow Sweep",
                onClick = { },
                backgroundBrush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    )
                )
            )

            GradientButton(
                text = "Center Radial",
                onClick = { },
                backgroundBrush = Brush.radialGradient(
                    colors = listOf(Color.Yellow, Color.Magenta, Color.Red),
                    radius = 200f
                )
            )
        }
    }
}

@Preview(name = "All Buttons Comparison", showBackground = true)
@Composable
private fun AllButtonsComparisonPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("All Button Types", style = MaterialTheme.typography.titleMedium)

            // Standard Material 3 Button with common styling
            Button(
                onClick = { },
                modifier = Modifier.commonButtonStyle(),
                colors = ButtonDefaults.buttonColors(),
                shape = CommonButtonDefaults.commonShape
            ) {
                Text("Standard M3 Button")
            }

            AppButton(
                text = "App Button",
                onClick = { }
            )

            GradientButton(
                text = "Gradient Button",
                onClick = { }
            )

            // Comparison in disabled state
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Disabled States", style = MaterialTheme.typography.titleSmall)

            Button(
                onClick = { },
                enabled = false,
                modifier = Modifier.commonButtonStyle(),
                colors = ButtonDefaults.buttonColors(),
                shape = CommonButtonDefaults.commonShape
            ) {
                Text("Disabled M3")
            }

            AppButton(
                text = "Disabled App Button",
                onClick = { },
                enabled = false
            )
        }
    }
}

@Preview(name = "⚠️ Deprecated Functions (Migration Guide)", showBackground = true)
@Composable
private fun DeprecatedFunctionsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "⚠️ DEPRECATED - Use for Migration Reference Only",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )

            // Old NavigationBackButton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackButton(onClick = { })
                Text("→ Use IconButton directly", fontSize = 12.sp)
            }

            // Old CommonButton with composable content
            CommonButton(
                onClick = { },
                text = {
                    Text("Old CommonButton", color = MaterialTheme.colorScheme.surface)
                }
            )
            Text("↑ Use Material 3 Button or AppButton instead", fontSize = 12.sp)

            // Old CommonButton with string
            CommonButton(
                text = "Old String CommonButton",
                onClick = { }
            )
            Text("↑ Use AppButton instead", fontSize = 12.sp)

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                "✅ RECOMMENDED REPLACEMENTS",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Recommended replacement for NavigationBackButton
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(0.035f),
                        shape = MaterialTheme.shapes.large
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "back",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text("↑ Direct IconButton replacement", fontSize = 12.sp)

            // Recommended replacement - AppButton
            AppButton(
                text = "New AppButton",
                onClick = { }
            )
            Text("↑ AppButton replacement", fontSize = 12.sp)

            // Recommended replacement - Direct M3 Button
            Button(
                onClick = { },
                modifier = Modifier.commonButtonStyle(),
                colors = ButtonDefaults.buttonColors(),
                shape = CommonButtonDefaults.commonShape
            ) {
                Text("Direct M3 Button")
            }
            Text("↑ Direct Material 3 Button", fontSize = 12.sp)
        }
    }
}