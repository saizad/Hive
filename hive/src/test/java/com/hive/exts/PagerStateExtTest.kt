package com.hive.exts

import androidx.compose.foundation.pager.PagerState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PagerStateExtTest {
    private lateinit var pagerState: PagerState

    @Before
    fun setup() {
        pagerState = mockk(relaxed = true)
    }

    @Test
    fun test_next_page_navigation() = runBlocking {
        
        every { pagerState.currentPage } returns 1
        
        
        pagerState.next()
        
        
        coVerify { pagerState.scrollToPage(2) }
    }

    @Test
    fun test_animated_next_page_navigation() = runBlocking {
        
        every { pagerState.currentPage } returns 1
        
        
        pagerState.animateToNext()
        
        
        coVerify { pagerState.animateScrollToPage(2) }
    }

    @Test
    fun test_previous_page_navigation() = runBlocking {
        
        every { pagerState.currentPage } returns 1
        
        
        pagerState.prev()
        
        
        coVerify { pagerState.scrollToPage(0) }
    }

    @Test
    fun test_animated_previous_page_navigation() = runBlocking {
        
        every { pagerState.currentPage } returns 1
        
        
        pagerState.animateToPrev()
        
        
        coVerify { pagerState.animateScrollToPage(0) }
    }

    @Test
    fun test_isfirst_property_when_on_first_page() {
        
        every { pagerState.currentPage } returns 0
        
        
        assertTrue(pagerState.isFirst)
    }

    @Test
    fun test_isfirst_property_when_not_on_first_page() {
        
        every { pagerState.currentPage } returns 1
        
        
        assertFalse(pagerState.isFirst)
    }

    @Test
    fun test_islast_property_when_on_last_page() {
        
        every { pagerState.currentPage } returns 2
        every { pagerState.pageCount } returns 3
        
        
        assertTrue(pagerState.isLast)
    }

    @Test
    fun test_islast_property_when_not_on_last_page() {
        
        every { pagerState.currentPage } returns 1
        every { pagerState.pageCount } returns 3
        
        
        assertFalse(pagerState.isLast)
    }
}