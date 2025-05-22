package com.hive.exts

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

class ComposeExtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPxToDpConversion() {
        val density = Density(density = 2f)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalDensity provides density) {
                assertEquals(50.dp, 100.pxToDp())
                assertEquals(25.dp, 50.pxToDp())
                assertEquals(0.dp, 0.pxToDp())
            }
        }
    }


    @Test
    fun customTimePickerDialog_confirmButton_callsOnConfirmAndDismiss() {
        var confirmedHour: Int? = null
        var confirmedMinute: Int? = null
        var dismissed = false

        composeTestRule.setContent {
            CustomTimePickerDialog(
                onDismiss = { dismissed = true },
                onConfirm = { hour, min ->
                    confirmedHour = hour
                    confirmedMinute = min
                }
            )
        }

        composeTestRule.onNodeWithText("OK").performClick()

        assert(confirmedHour != null)
        assert(confirmedMinute != null)
        assert(dismissed)
    }

    @Test
    fun customTimePickerDialog_cancelButton_callsOnDismissOnly() {
        var dismissed = false

        composeTestRule.setContent {
            CustomTimePickerDialog(
                onDismiss = { dismissed = true },
                onConfirm = { _, _ ->
                    fail("onConfirm should not be called when Cancel is clicked")
                }
            )
        }

        composeTestRule.onNodeWithText("Cancel").performClick()

        assert(dismissed)
    }

}