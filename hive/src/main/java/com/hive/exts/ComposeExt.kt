package com.hive.exts

import android.content.DialogInterface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

enum class ShadowDirection {
    Top, Bottom, Start, End
}


@Composable
fun Modifier.directionShadow(
    visible: Boolean = true,
    directions: List<ShadowDirection> = listOf(ShadowDirection.Bottom),
    shadowHeight: Dp = 10.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.1f)
): Modifier = this.then(
    if (!visible) Modifier else Modifier.drawWithContent {
        drawContent()

        val shadowPx = shadowHeight.toPx()

        directions.forEach { direction ->
            when (direction) {
                ShadowDirection.Top -> drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(shadowColor, Color.Transparent),
                        startY = 0f,
                        endY = shadowPx
                    ),
                    size = Size(size.width, shadowPx),
                    topLeft = Offset(0f, 0f)
                )

                ShadowDirection.Bottom -> drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(shadowColor, Color.Transparent),
                        startY = size.height - shadowPx,
                        endY = size.height
                    ),
                    size = Size(size.width, shadowPx),
                    topLeft = Offset(0f, size.height - shadowPx)
                )

                ShadowDirection.Start -> drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(shadowColor, Color.Transparent),
                        startX = 0f,
                        endX = shadowPx
                    ),
                    size = Size(shadowPx, size.height),
                    topLeft = Offset(0f, 0f)
                )

                ShadowDirection.End -> drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(shadowColor, Color.Transparent),
                        startX = size.width - shadowPx,
                        endX = size.width
                    ),
                    size = Size(shadowPx, size.height),
                    topLeft = Offset(size.width - shadowPx, 0f)
                )
            }
        }
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, min: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Select Time") },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}
@Composable
fun NegativePositiveButtons(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    negative: String? = "Cancel",
    positive: String,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        negative?.let {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = {
                        onClick(DialogInterface.BUTTON_NEGATIVE)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 16.sp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick = {
                    onClick(DialogInterface.BUTTON_POSITIVE)
                },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = positive,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 16.sp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                )
            }
        }
    }

}

@Composable
fun WarningLayout(
    modifier: Modifier = Modifier,
    message: String,
    enabled: Boolean = true,
    negative: String? = "Cancel",
    positive: String,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.extraLarge)
            .padding(15.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            modifier = Modifier.testTag("WarningIcon")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 16.sp),
            modifier = Modifier.testTag("WarningMessage")
        )

        Spacer(modifier = Modifier.height(24.dp))

        NegativePositiveButtons(
            positive = positive,
            negative = negative,
            enabled = enabled,
            onClick = onClick,
            modifier = Modifier.testTag("Buttons")
        )
    }
}
