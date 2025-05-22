package com.hive

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@ExperimentalCoroutinesApi
class SplashViewModelTest () {

    val viewModel = SplashViewModel()

    @Test
    fun `user flow emits expected pair`() = runTest {
        viewModel.user.test {
            val emission = awaitItem()
            assertEquals("Andre" to 2, emission)
            awaitComplete()
        }
    }


    @Test
    fun isOdd_checks() {
        assertTrue(viewModel.isOdd(3))
        assertFalse(viewModel.isOdd(2))
    }
}
