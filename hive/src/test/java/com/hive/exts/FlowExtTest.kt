package com.hive.exts

import app.cash.turbine.test
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowExtTest {

    @Test
    fun throttlefirst_emits_first_item_per_period() = runTest {
        val flow = flow {
            emit(1)
            delay(50)
            emit(2)
            delay(50)
            emit(3)
            delay(200)
            emit(4)
        }.throttleFirst(100)

        flow.test {
            assertEquals(1, awaitItem()) // First item should be emitted immediately
            assertEquals(3, awaitItem()) // Skips 2, emits 3 after 100ms
            assertEquals(4, awaitItem()) // Emits 4 after 200ms
            awaitComplete()
        }
    }

    @Test
    fun combinefirst_emits_values_from_first_flow() = runTest {
        val flow1 = flowOf(1, 2, 3)
        val flow2 = flowOf("A", "B", "C")
        val result = flow1.combineFirst(flow2).toList()
        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun combinesecond_emits_values_from_second_flow() = runTest {
        val flow1 = flowOf(1, 2, 3)
        val flow2 = flowOf("A", "B", "C")
        val result = flow1.combineSecond(flow2).toList()
        assertEquals(listOf("A", "B", "C"), result)
    }

    @Test
    fun combinepair_pairs_elements_from_both_flows() = runTest {
        val flow1 = flowOf(1, 2, 3)
        val flow2 = flowOf("A", "B", "C")
        val result = flow1.combinePair(flow2).toList()
        assertEquals(listOf(1 to "A", 2 to "B", 3 to "C"), result)
    }

    @Test
    fun combinecollect_applies_transformation() = runTest {
        val flow1 = flowOf(1, 2, 3)
        val flow2 = flowOf(10, 20, 30)
        val collected = mutableListOf<Int>()
        flow1.combineCollect(flow2) { a, b -> collected.add(a + b) }
        assertEquals(listOf(11, 22, 33), collected)
    }

    @Test
    fun intervalflow_emits_values_until_duration() = runTest {
        val duration = 300L
        val interval = 100L
        val results = intervalFlow(duration, interval).toList()
        assert(results.size >= 3) { "Expected at least 3 emissions, got ${results.size}" }
    }

    @Test
    fun intervalflow_infinite_emits_unit_values() = runTest {
        val flow = intervalFlow(100).take(3).toList()
        assertEquals(listOf(Unit, Unit, Unit), flow)
    }

    @Test
    fun mutablestateflow_emitrefresh_increments_value() = runTest {
        val stateFlow = MutableStateFlow(0)
        stateFlow.emitRefresh
        assertEquals(1, stateFlow.value)
        stateFlow.emitRefresh
        assertEquals(2, stateFlow.value)
    }
}
