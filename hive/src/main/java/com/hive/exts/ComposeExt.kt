package com.hive.exts

import android.content.DialogInterface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// MARK: - Density Extensions
@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

// MARK: - Shadow System
enum class ShadowDirection {
    Top, Bottom, Start, End, All
}

data class ShadowConfig(
    val visible: Boolean = true,
    val directions: List<ShadowDirection> = listOf(ShadowDirection.Bottom),
    val shadowHeight: Dp = 10.dp,
    val shadowColor: Color = Color.Black.copy(alpha = 0.1f),
    val fadeIntensity: Float = 1f
) {
    companion object {
        fun allDirections(
            shadowHeight: Dp = 10.dp,
            shadowColor: Color = Color.Black.copy(alpha = 0.1f)
        ) = ShadowConfig(
            directions = listOf(ShadowDirection.All),
            shadowHeight = shadowHeight,
            shadowColor = shadowColor
        )

        fun bottomOnly(
            shadowHeight: Dp = 10.dp,
            shadowColor: Color = Color.Black.copy(alpha = 0.1f)
        ) = ShadowConfig(
            directions = listOf(ShadowDirection.Bottom),
            shadowHeight = shadowHeight,
            shadowColor = shadowColor
        )
    }
}

@Composable
fun Modifier.directionShadow(config: ShadowConfig): Modifier = this.then(
    if (!config.visible) Modifier else Modifier.drawWithContent {
        drawContent()
        applyShadow(config)
    }
)

// Backward compatibility
@Composable
fun Modifier.directionShadow(
    visible: Boolean = true,
    directions: List<ShadowDirection> = listOf(ShadowDirection.Bottom),
    shadowHeight: Dp = 10.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.1f)
): Modifier = directionShadow(
    ShadowConfig(visible, directions, shadowHeight, shadowColor)
)

private fun androidx.compose.ui.graphics.drawscope.DrawScope.applyShadow(config: ShadowConfig) {
    val shadowPx = config.shadowHeight.toPx()
    val directions = if (config.directions.contains(ShadowDirection.All)) {
        listOf(ShadowDirection.Top, ShadowDirection.Bottom, ShadowDirection.Start, ShadowDirection.End)
    } else {
        config.directions
    }

    directions.forEach { direction ->
        when (direction) {
            ShadowDirection.Top -> drawTopShadow(shadowPx, config.shadowColor)
            ShadowDirection.Bottom -> drawBottomShadow(shadowPx, config.shadowColor)
            ShadowDirection.Start -> drawStartShadow(shadowPx, config.shadowColor)
            ShadowDirection.End -> drawEndShadow(shadowPx, config.shadowColor)
            ShadowDirection.All -> { /* Handled above */ }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTopShadow(shadowPx: Float, shadowColor: Color) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(shadowColor, Color.Transparent),
            startY = 0f,
            endY = shadowPx
        ),
        size = Size(size.width, shadowPx),
        topLeft = Offset(0f, 0f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBottomShadow(shadowPx: Float, shadowColor: Color) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, shadowColor),
            startY = size.height - shadowPx,
            endY = size.height
        ),
        size = Size(size.width, shadowPx),
        topLeft = Offset(0f, size.height - shadowPx)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStartShadow(shadowPx: Float, shadowColor: Color) {
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(shadowColor, Color.Transparent),
            startX = 0f,
            endX = shadowPx
        ),
        size = Size(shadowPx, size.height),
        topLeft = Offset(0f, 0f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEndShadow(shadowPx: Float, shadowColor: Color) {
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(Color.Transparent, shadowColor),
            startX = size.width - shadowPx,
            endX = size.width
        ),
        size = Size(shadowPx, size.height),
        topLeft = Offset(size.width - shadowPx, 0f)
    )
}

// MARK: - Dialog System
sealed class DialogAction(val id: Int, val label: String) {
    data class Positive(val text: String) : DialogAction(DialogInterface.BUTTON_POSITIVE, text)
    data class Negative(val text: String) : DialogAction(DialogInterface.BUTTON_NEGATIVE, text)
    data class Neutral(val text: String) : DialogAction(DialogInterface.BUTTON_NEUTRAL, text)
}

data class TimePickerConfig(
    val title: String = "Select Time",
    val positiveAction: DialogAction.Positive = DialogAction.Positive("OK"),
    val negativeAction: DialogAction.Negative? = DialogAction.Negative("Cancel"),
    val initialHour: Int = 12,
    val initialMinute: Int = 0,
    val is24Hour: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    config: TimePickerConfig = TimePickerConfig(),
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = config.initialHour,
        initialMinute = config.initialMinute,
        is24Hour = config.is24Hour
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
                onDismiss()
            }) {
                Text(config.positiveAction.label)
            }
        },
        dismissButton = config.negativeAction?.let { action ->
            {
                TextButton(onClick = onDismiss) {
                    Text(action.label)
                }
            }
        },
        title = { Text(config.title) },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

// Backward compatibility
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, min: Int) -> Unit
) = CustomTimePickerDialog(
    config = TimePickerConfig(),
    onDismiss = onDismiss,
    onConfirm = onConfirm
)

// MARK: - Button System
data class ButtonConfig(
    val text: String,
    val enabled: Boolean = true,
    val style: ButtonStyle = ButtonStyle.Outlined,
    val textStyle: TextStyle? = null,
    val brush: Brush? = null,
    val testTag: String? = null
)

enum class ButtonStyle {
    Filled, Outlined, Text, Elevated, FilledTonal
}

data class ButtonPairConfig(
    val positive: ButtonConfig,
    val negative: ButtonConfig? = null,
    val spacing: Dp = 16.dp,
    val arrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp)
)

@Composable
fun ActionButton(
    config: ButtonConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonModifier = modifier
        .let { if (config.testTag != null) it.testTag(config.testTag) else it }

    when (config.style) {
        ButtonStyle.Filled -> Button(
            onClick = onClick,
            enabled = config.enabled,
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large
        ) {
            ButtonText(config)
        }

        ButtonStyle.Outlined -> OutlinedButton(
            onClick = onClick,
            enabled = config.enabled,
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large
        ) {
            ButtonText(config)
        }

        ButtonStyle.Text -> TextButton(
            onClick = onClick,
            enabled = config.enabled,
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large
        ) {
            ButtonText(config)
        }

        ButtonStyle.Elevated -> ElevatedButton(
            onClick = onClick,
            enabled = config.enabled,
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large
        ) {
            ButtonText(config)
        }

        ButtonStyle.FilledTonal -> FilledTonalButton(
            onClick = onClick,
            enabled = config.enabled,
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large
        ) {
            ButtonText(config)
        }
    }
}

@Composable
private fun ButtonText(config: ButtonConfig) {
    val baseStyle = config.textStyle ?: MaterialTheme.typography.labelSmall.copy(fontSize = 16.sp)
    val finalStyle = config.brush?.let { brush ->
        baseStyle.copy(brush = brush)
    } ?: baseStyle.copy(color = MaterialTheme.colorScheme.onSurface)

    Text(
        text = config.text,
        style = finalStyle
    )
}

@Composable
fun ButtonPair(
    config: ButtonPairConfig,
    onAction: (DialogAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = config.arrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        config.negative?.let { negativeConfig ->
            Box(modifier = Modifier.weight(1f)) {
                ActionButton(
                    config = negativeConfig,
                    onClick = { onAction(DialogAction.Negative(negativeConfig.text)) },
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            ActionButton(
                config = config.positive,
                onClick = { onAction(DialogAction.Positive(config.positive.text)) },
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            )
        }
    }
}

// Backward compatibility
@Composable
fun NegativePositiveButtons(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    negative: String? = "Cancel",
    positive: String,
    onClick: (Int) -> Unit
) {
    val gradientBrush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    val config = ButtonPairConfig(
        positive = ButtonConfig(
            text = positive,
            enabled = enabled,
            brush = gradientBrush
        ),
        negative = negative?.let {
            ButtonConfig(
                text = it,
                brush = gradientBrush
            )
        }
    )

    ButtonPair(
        config = config,
        onAction = { action -> onClick(action.id) },
        modifier = modifier
    )
}

// MARK: - Alert System
data class AlertConfig(
    val icon: ImageVector = Icons.Filled.Warning,
    val iconTint: Color? = null,
    val message: String,
    val messageStyle: TextStyle? = null,
    val messageAlignment: TextAlign = TextAlign.Center,
    val backgroundColor: Color? = null,
    val shape: androidx.compose.foundation.shape.RoundedCornerShape? = null,
    val padding: PaddingValues = PaddingValues(15.dp),
    val spacing: Dp = 8.dp,
    val buttonSpacing: Dp = 24.dp,
    val testTags: AlertTestTags = AlertTestTags()
)

data class AlertTestTags(
    val icon: String = "AlertIcon",
    val message: String = "AlertMessage",
    val buttons: String = "AlertButtons"
)

@Composable
fun AlertLayout(
    config: AlertConfig,
    buttonConfig: ButtonPairConfig,
    onAction: (DialogAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = config.backgroundColor ?: MaterialTheme.colorScheme.surface,
                shape = config.shape ?: MaterialTheme.shapes.extraLarge
            )
            .padding(config.padding)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = config.icon,
            contentDescription = null,
            modifier = Modifier.testTag(config.testTags.icon)
        )

        Spacer(modifier = Modifier.height(config.spacing))

        Text(
            text = config.message,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = config.messageAlignment,
            style = config.messageStyle ?: MaterialTheme.typography.labelSmall.copy(fontSize = 16.sp),
            modifier = Modifier.testTag(config.testTags.message)
        )

        Spacer(modifier = Modifier.height(config.buttonSpacing))

        ButtonPair(
            config = buttonConfig,
            onAction = onAction,
            modifier = Modifier.testTag(config.testTags.buttons)
        )
    }
}

// Backward compatibility
@Composable
fun WarningLayout(
    modifier: Modifier = Modifier,
    message: String,
    enabled: Boolean = true,
    negative: String? = "Cancel",
    positive: String,
    onClick: (Int) -> Unit
) {
    val gradientBrush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    val alertConfig = AlertConfig(
        message = message,
        testTags = AlertTestTags(
            icon = "WarningIcon",
            message = "WarningMessage",
            buttons = "Buttons"
        )
    )

    val buttonConfig = ButtonPairConfig(
        positive = ButtonConfig(
            text = positive,
            enabled = enabled,
            brush = gradientBrush
        ),
        negative = negative?.let {
            ButtonConfig(
                text = it,
                brush = gradientBrush
            )
        }
    )

    AlertLayout(
        config = alertConfig,
        buttonConfig = buttonConfig,
        onAction = { action -> onClick(action.id) },
        modifier = modifier
    )
}