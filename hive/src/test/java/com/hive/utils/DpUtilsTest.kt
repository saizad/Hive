package com.hive.utils

import com.intuit.sdp.R
import org.junit.Assert.assertEquals
import org.junit.Test

class DpUtilsTest {
    
    @Test
    fun test_all_valid_dp_sizes() {
        // Test all possible values from 1 to 500
        for (size in 1..500) {
            val expectedResource = R.dimen::class.java.getField("_${size}sdp").getInt(null)
            assertEquals(
                "Failed for size: $size",
                expectedResource,
                getDimenResource(size)
            )
        }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun test_size_below_minimum_throws_exception() {
        getDimenResource(0)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun test_size_above_maximum_throws_exception() {
        getDimenResource(501)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun test_negative_size_throws_exception() {
        getDimenResource(-1)
    }
}