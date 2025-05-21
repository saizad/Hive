package com.hive.utils

import com.intuit.ssp.R
import org.junit.Assert.assertEquals
import org.junit.Test

class TextUnitUtilsTest {
    
    @Test
    fun test_all_valid_text_unit_sizes() {
        // Test all possible values from 8 to 100
        for (size in 8..100) {
            val expectedResource = R.dimen::class.java.getField("_${size}ssp").getInt(null)
            assertEquals(
                "Failed for size: $size",
                expectedResource,
                getTextUnitDimenResource(size)
            )
        }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun test_size_below_minimum_throws_exception() {
        getTextUnitDimenResource(7)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun test_size_above_maximum_throws_exception() {
        getTextUnitDimenResource(101)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun test_negative_size_throws_exception() {
        getTextUnitDimenResource(-1)
    }
} 