package com.hive.exts


import androidx.compose.foundation.pager.PagerState

suspend fun PagerState.next() {
    this.scrollToPage(currentPage + 1)
}

suspend fun PagerState.animateToNext() {
    this.animateScrollToPage(currentPage + 1)
}

suspend fun PagerState.prev() {
    this.scrollToPage(currentPage - 1)
}

suspend fun PagerState.animateToPrev() {
    this.animateScrollToPage(currentPage - 1)
}

val PagerState.isFirst get() = currentPage == 0

val PagerState.isLast get() = currentPage == pageCount - 1
