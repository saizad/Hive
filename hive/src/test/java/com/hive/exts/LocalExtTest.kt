package com.hive.exts

import io.mockk.every
import io.mockk.mockkStatic
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class LocalExtTest {

    @Test
    fun test_double_toamount() {
        val amount = 12345.0
        val result = amount.toAmount()
        assertEquals("₹12,345", result) // Adjust for expected currency formatting in hi_IN locale
    }

    @Test
    fun test_double_toamount_with_us_locale() {
        val amount = 12345.0
        val result = amount.toAmount(Locale.US)
        assertEquals("$12,345", result) // Adjust for expected currency formatting in US locale
    }

    @Test
    fun test_int_toamount() {
        val amount = 12345
        val result = amount.toAmount()
        assertEquals("₹12,345", result) // Adjust for expected currency formatting in hi_IN locale
    }

    @Test
    fun test_int_toamount_with_us_locale() {
        val amount = 12345
        val result = amount.toAmount(Locale.US)
        assertEquals("$12,345", result) // Adjust for expected currency formatting in US locale
    }

    @Test
    fun test_number_toreadablenumber() {
        val number = 1234567890
        val result = number.toReadableNumber()
        assertEquals("1,23,45,67,890", result) // Adjust for expected number formatting in hi_IN locale
    }

    @Test
    fun test_number_toreadablenumber_with_us_locale() {
        val number = 1234567890
        val result = number.toReadableNumber(Locale.US)
        assertEquals("1,234,567,890", result)
    }

    @Test
    fun test_number_toreadablenumber_with_in_locale() {
        val number = 1234567890
        val locale = Locale("en", "IN")
        val result = number.toReadableNumber(locale)
        assertEquals("1,23,45,67,890", result)
    }

    @Test
    fun test_datetime_greetings() {
        // Night time (0-3)
        assertEquals("Good Night", DateTime.now().withTime(0, 0, 0, 0).greetings)
        assertEquals("Good Night", DateTime.now().withTime(3, 59, 59, 0).greetings)

        // Morning time (4-11)
        assertEquals("Good Morning", DateTime.now().withTime(4, 0, 0, 0).greetings)
        assertEquals("Good Morning", DateTime.now().withTime(11, 59, 59, 0).greetings)

        // Afternoon time (12-16)
        assertEquals("Good Afternoon", DateTime.now().withTime(12, 0, 0, 0).greetings)
        assertEquals("Good Afternoon", DateTime.now().withTime(16, 59, 59, 0).greetings)

        // Evening time (17-20)
        assertEquals("Good Evening", DateTime.now().withTime(17, 0, 0, 0).greetings)
        assertEquals("Good Evening", DateTime.now().withTime(20, 59, 59, 0).greetings)

        // Night time (21-23)
        assertEquals("Good Night", DateTime.now().withTime(21, 0, 0, 0).greetings)
        assertEquals("Good Night", DateTime.now().withTime(23, 59, 59, 0).greetings)

        assertEquals("Good Night", DateTime.now().withTime(0, 0, 0, 0).greetings)  // Start of night (early)
        assertEquals("Good Night", DateTime.now().withTime(1, 0, 0, 0).greetings)  // Start of night (early)
        assertEquals("Good Night", DateTime.now().withTime(2, 0, 0, 0).greetings)  // Start of night (early)
        assertEquals("Good Night", DateTime.now().withTime(3, 0, 0, 0).greetings)  // During night (early)
        assertEquals("Good Morning", DateTime.now().withTime(4, 0, 0, 0).greetings)  // Start of morning
        assertEquals("Good Morning", DateTime.now().withTime(5, 0, 0, 0).greetings)  // Start of morning
        assertEquals("Good Morning", DateTime.now().withTime(6, 0, 0, 0).greetings)  // Start of morning
        assertEquals("Good Morning", DateTime.now().withTime(7, 0, 0, 0).greetings)  // Start of morning
        assertEquals("Good Morning", DateTime.now().withTime(8, 0, 0, 0).greetings)  // Start of morning
        assertEquals("Good Morning", DateTime.now().withTime(9, 0, 0, 0).greetings)  // Start of morning
        assertEquals("Good Morning", DateTime.now().withTime(10, 0, 0, 0).greetings)  // Start of morning
        assertEquals("Good Morning", DateTime.now().withTime(11, 0, 0, 0).greetings)  // During morning
        assertEquals("Good Afternoon", DateTime.now().withTime(12, 0, 0, 0).greetings)  // Start of afternoon
        assertEquals("Good Afternoon", DateTime.now().withTime(13, 0, 0, 0).greetings)  // Start of afternoon
        assertEquals("Good Afternoon", DateTime.now().withTime(14, 0, 0, 0).greetings)  // Start of afternoon
        assertEquals("Good Afternoon", DateTime.now().withTime(15, 0, 0, 0).greetings)  // Start of afternoon
        assertEquals("Good Afternoon", DateTime.now().withTime(16, 0, 0, 0).greetings)  // During afternoon
        assertEquals("Good Evening", DateTime.now().withTime(17, 0, 0, 0).greetings)  // Start of evening
        assertEquals("Good Evening", DateTime.now().withTime(18, 0, 0, 0).greetings)  // Start of evening
        assertEquals("Good Evening", DateTime.now().withTime(19, 0, 0, 0).greetings)  // Start of evening
        assertEquals("Good Evening", DateTime.now().withTime(20, 0, 0, 0).greetings)  // End of evening
        assertEquals("Good Night", DateTime.now().withTime(21, 0, 0, 0).greetings)  // Start of night (late)
        assertEquals("Good Night", DateTime.now().withTime(22, 0, 0, 0).greetings)  // Start of night (late)
        assertEquals("Good Night", DateTime.now().withTime(23, 0, 0, 0).greetings)  // During night (late)
    }

    @Test
    fun test_global_greetings() {
        mockkStatic(DateTime::class)

        // Test boundary conditions
        val times = listOf(
            0 to "Good Night",   // Start of early night
            3 to "Good Night",   // End of early night
            4 to "Good Morning", // Start of morning
            11 to "Good Morning", // End of morning
            12 to "Good Afternoon", // Start of afternoon
            16 to "Good Afternoon", // End of afternoon
            17 to "Good Evening", // Start of evening
            20 to "Good Evening", // End of evening
            21 to "Good Night",  // Start of late night
            23 to "Good Night"   // End of late night
        )

        times.forEach { (hour, expected) ->
            val timeToTest = DateTime().withTime(hour, 0, 0, 0)
            every { DateTime.now() } returns timeToTest
            assertEquals(expected, greetings)
        }
    }

}
