package com.hive.exts

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
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
}