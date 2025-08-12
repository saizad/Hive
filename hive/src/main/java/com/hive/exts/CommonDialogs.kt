package com.hive.exts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

object CommonDialogs {
    const val LOADING_DIALOG = "loading_dialog"
    const val LOADING_TEXT = "loading_text"
}

/**
 * A customizable loading dialog composable that displays a circular progress indicator
 * with optional loading text and various styling options.
 *
 * @param show Whether to display the loading dialog
 * @param modifier Additional modifier to be applied to the progress indicator
 * @param loadingText Optional text to display below the progress indicator
 * @param backgroundColor Background color of the dialog container
 * @param progressColor Color of the progress indicator
 * @param textColor Color of the loading text
 * @param textStyle Style for the loading text
 * @param containerShape Shape of the dialog container
 * @param containerPadding Padding inside the dialog container
 * @param progressSize Size of the progress indicator
 * @param strokeWidth Width of the progress indicator stroke
 * @param dismissible Whether the dialog can be dismissed by tapping outside
 * @param testTag Test tag for the progress indicator (for UI testing)
 * @param textTestTag Test tag for the loading text (for UI testing)
 */
@Composable
fun ShowLoading(
    show: Boolean,
    modifier: Modifier = Modifier,
    loadingText: String? = null,
    backgroundColor: Color = Color.White,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    containerShape: Shape = MaterialTheme.shapes.large,
    containerPadding: Dp = 32.dp,
    progressSize: Dp = 60.dp,
    strokeWidth: Dp = 10.dp,
    dismissible: Boolean = false,
    testTag: String = CommonDialogs.LOADING_DIALOG,
    textTestTag: String = CommonDialogs.LOADING_TEXT
) {
    if (show) {
        Dialog(
            onDismissRequest = { if (dismissible) { /* Handle dismiss */ } },
            properties = DialogProperties(
                dismissOnBackPress = dismissible,
                dismissOnClickOutside = dismissible
            )
        ) {
            Box(
                modifier = Modifier
                    .background(backgroundColor, containerShape)
                    .padding(containerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(progressSize)
                            .then(modifier)
                            .testTag(testTag),
                        strokeWidth = strokeWidth,
                        color = progressColor
                    )

                    loadingText?.let { text ->
                        Text(
                            text = text,
                            style = textStyle,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag(textTestTag)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Simplified version of ShowLoading with preset configurations for common use cases
 */
object LoadingPresets {

    /**
     * Small loading indicator without text
     */
    @Composable
    fun Small(
        show: Boolean,
        modifier: Modifier = Modifier,
        progressColor: Color = MaterialTheme.colorScheme.primary
    ) {
        ShowLoading(
            show = show,
            modifier = modifier,
            progressSize = 32.dp,
            strokeWidth = 4.dp,
            containerPadding = 16.dp,
            progressColor = progressColor
        )
    }

    /**
     * Large loading indicator with customizable text
     */
    @Composable
    fun Large(
        show: Boolean,
        loadingText: String = "Loading...",
        modifier: Modifier = Modifier,
        progressColor: Color = MaterialTheme.colorScheme.primary
    ) {
        ShowLoading(
            show = show,
            loadingText = loadingText,
            modifier = modifier,
            progressSize = 80.dp,
            strokeWidth = 8.dp,
            containerPadding = 32.dp,
            progressColor = progressColor,
            textStyle = MaterialTheme.typography.titleMedium
        )
    }

    /**
     * Dark themed loading indicator
     */
    @Composable
    fun Dark(
        show: Boolean,
        loadingText: String? = null,
        modifier: Modifier = Modifier
    ) {
        ShowLoading(
            show = show,
            loadingText = loadingText,
            modifier = modifier,
            backgroundColor = Color.Black.copy(alpha = 0.8f),
            progressColor = Color.White,
            textColor = Color.White
        )
    }

    /**
     * Transparent background loading indicator
     */
    @Composable
    fun Transparent(
        show: Boolean,
        loadingText: String? = null,
        modifier: Modifier = Modifier,
        progressColor: Color = MaterialTheme.colorScheme.primary
    ) {
        ShowLoading(
            show = show,
            loadingText = loadingText,
            modifier = modifier,
            backgroundColor = Color.Transparent,
            progressColor = progressColor,
            containerShape = RoundedCornerShape(0.dp)
        )
    }
}