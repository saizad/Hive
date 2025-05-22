package com.hive.exts

import android.content.DialogInterface
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class WarningLayoutTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun warningLayout_showsIconAndMessage() {
        composeTestRule.setContent {
            WarningLayout(
                message = "Are you sure?",
                positive = "Yes",
                onClick = {}
            )
        }

        composeTestRule.onNodeWithTag("WarningIcon").assertExists()
        composeTestRule.onNodeWithTag("WarningMessage").assertTextEquals("Are you sure?")
    }

    @Test
    fun warningLayout_showsBothButtons() {
        composeTestRule.setContent {
            WarningLayout(
                message = "Delete item?",
                positive = "Delete",
                negative = "Cancel",
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Delete").assertExists()
        composeTestRule.onNodeWithText("Cancel").assertExists()
    }

    @Test
    fun warningLayout_onlyShowsPositiveButton_ifNegativeIsNull() {
        composeTestRule.setContent {
            WarningLayout(
                message = "Proceed?",
                positive = "Yes",
                negative = null,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Yes").assertExists()
        composeTestRule.onNodeWithText("Cancel").assertDoesNotExist()
    }

    @Test
    fun warningLayout_buttonsAreDisabledWhenEnabledIsFalse() {
        composeTestRule.setContent {
            WarningLayout(
                message = "Confirm?",
                positive = "OK",
                enabled = false,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("OK").assertIsNotEnabled()
    }

    @Test
    fun warningLayout_triggersCorrectClickIndex() {
        var clicked = -1
        composeTestRule.setContent {
            WarningLayout(
                message = "Proceed?",
                positive = "Yes",
                negative = "No",
                onClick = { clicked = it }
            )
        }

        composeTestRule.onNodeWithText("No").performClick()
        assert(clicked == DialogInterface.BUTTON_NEGATIVE)

        composeTestRule.onNodeWithText("Yes").performClick()
        assert(clicked == DialogInterface.BUTTON_POSITIVE)
    }

}