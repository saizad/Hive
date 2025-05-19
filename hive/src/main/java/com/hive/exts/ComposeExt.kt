package com.hive.exts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.exela.teacher.ui.theme.space1x
import com.exela.teacher.ui.theme.space2x

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

enum class ShadowDirection {
    Top, Bottom, Start, End
}


@Composable
fun Modifier.directionShadow(
    visible: Boolean = true,
    directions: List<ShadowDirection> = listOf(ShadowDirection.Bottom),
    shadowHeight: Dp = space1x(),
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
