package com.hive.exts

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test


class DateTimeExtTest {
    
    private val testDate = DateTime(2023, 10, 5, 14, 30, 0) 
    private val testDateUTC = DateTime(2023, 10, 5, 14, 30, 0, DateTimeZone.UTC)

    
    @Test
    fun testOrdinal() {
        assertEquals("1st", ordinal(1))
        assertEquals("2nd", ordinal(2))
        assertEquals("3rd", ordinal(3))
        assertEquals("4th", ordinal(4))
        assertEquals("11th", ordinal(11))
        assertEquals("12th", ordinal(12))
        assertEquals("13th", ordinal(13))
        assertEquals("21st", ordinal(21))
        assertEquals("22nd", ordinal(22))
        assertEquals("23rd", ordinal(23))
        assertEquals("101st", ordinal(101))
    }

    
    @Test
    fun testDayMonthYear() {
        assertEquals("5 October, 2023", testDate.dayMonthYear)
    }

    
    @Test
    fun testArgsDate() {
        assertEquals("2023-10-05", testDate.argsDate)
    }

    
    @Test
    fun testArgsTime() {
        assertEquals("14:30", testDate.argsTime)
    }

    
    @Test
    fun testUtcTime() {
        assertEquals(testDateUTC, testDate.utcTime)
    }

    
    @Test
    fun testOrdinalYear() {
        assertEquals("5th October 2023", testDate.ordinalYear)
    }

    
    @Test
    fun testOrdinalYearShort() {
        assertEquals("5th Oct 23", testDate.ordinalYearShort.replace(Regex("[.]"), ""))
    }

    
    @Test
    fun testIsToday() {
        val today = DateTime.now().withTimeAtStartOfDay()
        assertTrue(today.isToday)
        assertFalse(today.minusDays(1).isToday)
    }

    
    @Test
    fun testTimeToToday() {
        val timeString = "14:30:00"
        val expectedTime = DateTime.now().withTime(14, 30, 0, 0)
        assertEquals(expectedTime, timeString.timeToToday)
    }

    
    @Test
    fun testFormattedDateShort() {
        assertEquals("5th Oct '23", testDate.formattedDateShort.replace(Regex("[.]"), ""))
    }

    
    @Test
    fun testTimeWithOrdinalDate() {
        assertEquals("02:30 PM 5th Oct 23", testDate.timeWithOrdinalDate.replace(Regex("[.]"), ""))
    }

    
    @Test
    fun testIsSameMonth() {
        val sameMonthDate = DateTime(2023, 10, 15, 0, 0)
        val differentMonthDate = DateTime(2023, 11, 5, 0, 0)
        assertTrue(testDate.isSameMonth(sameMonthDate))
        assertFalse(testDate.isSameMonth(differentMonthDate))
    }

    
    @Test
    fun testStartOfMonth() {
        assertEquals(DateTime(2023, 10, 1, 0, 0), testDate.startOfMonth)
    }

    @Test
    fun test_start_date_calculation_with_monday_as_start_day() {
        val year = 2025
        val month = 1
        val startDayOfWeek = 1 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)
        val expectedStartDate = LocalDate(2024, 12, 30) 

        assertEquals(expectedStartDate, dates.first())
    }

    @Test
    fun test_end_date_calculation_with_monday_as_start_day_and_6_rows() {
        val year = 2025
        val month = 1
        val startDayOfWeek = 1 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)
        val expectedEndDate = LocalDate(2025, 2, 9) 

        assertEquals(expectedEndDate, dates.last())
    }

    @Test
    fun test_number_of_dates_returned_matches_rows_and_columns() {
        val year = 2025
        val month = 1
        val startDayOfWeek = 1 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)

        assertEquals(6 * 7, dates.size) 
    }

    @Test
    fun test_calendar_alignment_with_sunday_as_start_day() {
        val year = 2025
        val month = 1
        val startDayOfWeek = 7 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)
        val expectedStartDate = LocalDate(2024, 12, 29) 

        assertEquals(expectedStartDate, dates.first())
    }

    @Test
    fun test_calendar_alignment_with_wednesday_as_start_day() {
        val year = 2025
        val month = 1
        val startDayOfWeek = 3 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)
        val expectedStartDate = LocalDate(2025, 1, 1) 

        assertEquals(expectedStartDate, dates.first())
    }

    @Test
    fun test_dates_include_full_month_of_january() {
        val year = 2025
        val month = 1
        val startDayOfWeek = 1 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)
        val januaryDates = dates.filter { it.monthOfYear == month }

        assertEquals(31, januaryDates.size) 
    }

    @Test
    fun test_dates_include_previous_and_next_month_days() {
        val year = 2025
        val month = 1
        val startDayOfWeek = 1 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)

        val previousMonthDates = dates.filter { it.monthOfYear == 12 }
        val nextMonthDates = dates.filter { it.monthOfYear == 2 }

        assertEquals(2, previousMonthDates.size) 
        assertEquals(9, nextMonthDates.size) 
    }

    @Test
    fun test_february_in_non_leap_year() {
        val year = 2025
        val month = 2 
        val startDayOfWeek = 1 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)
        val februaryDates = dates.filter { it.monthOfYear == month }

        assertEquals(28, februaryDates.size) 
    }

    @Test
    fun test_february_in_leap_year() {
        val year = 2024
        val month = 2 
        val startDayOfWeek = 1 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)
        val februaryDates = dates.filter { it.monthOfYear == month }

        assertEquals(29, februaryDates.size) 
    }

    @Test
    fun test_calendar_with_minimum_rows() {
        val year = 2025
        val month = 1
        val startDayOfWeek = 1 
        val numberOfRows = 1

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)

        assertEquals(7, dates.size) 
    }

    @Test
    fun test_calendar_with_maximum_rows() {
        val year = 2025
        val month = 1
        val startDayOfWeek = 1 
        val numberOfRows = 8

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)

        assertEquals(8 * 7, dates.size) 
    }

    @Test
    fun test_december_to_january_transition() {
        val year = 2025
        val month = 12 
        val startDayOfWeek = 1 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)
        val decemberDates = dates.filter { it.monthOfYear == 12 }
        val januaryDates = dates.filter { it.monthOfYear == 1 }

        assertEquals(31, decemberDates.size) 
        assertTrue(januaryDates.isNotEmpty()) 
    }

    @Test
    fun test_january_to_december_transition() {
        val year = 2025
        val month = 1 
        val startDayOfWeek = 1 
        val numberOfRows = 6

        val dates = generateCalendar(year, month, startDayOfWeek, numberOfRows)
        val januaryDates = dates.filter { it.monthOfYear == 1 }
        val decemberDates = dates.filter { it.monthOfYear == 12 }

        assertEquals(31, januaryDates.size) 
        assertTrue(decemberDates.isNotEmpty()) 
    }

    @Test(expected =  IllegalArgumentException::class)
    fun test_invalid_start_day_of_week() {

        val year = 2025
        val month = 1
        val invalidStartDayOfWeek = 0
        generateCalendar(year, month, invalidStartDayOfWeek, 6)
    }

    @Test
    fun single_month_range_should_return_one_date() {
        val date = DateTime(2024, 5, 1, 0, 0)
        val result = generateMonthsRange(date, date)
        assertEquals(listOf(date), result)
    }

    @Test
    fun multiple_months_range() {
        val startDate = DateTime(2024, 1, 1, 0, 0)
        val endDate = DateTime(2024, 4, 1, 0, 0)
        val expected = listOf(
            DateTime(2024, 1, 1, 0, 0),
            DateTime(2024, 2, 1, 0, 0),
            DateTime(2024, 3, 1, 0, 0),
            DateTime(2024, 4, 1, 0, 0)
        )
        val result = generateMonthsRange(startDate, endDate)
        assertEquals(expected, result)
    }

    @Test
    fun full_year_range() {
        val startDate = DateTime(2023, 1, 1, 0, 0)
        val endDate = DateTime(2023, 12, 1, 0, 0)
        val expected = (1..12).map { month -> DateTime(2023, month, 1, 0, 0) }
        val result = generateMonthsRange(startDate, endDate)
        assertEquals(expected, result)
    }

    @Test
    fun start_and_end_in_different_years() {
        val startDate = DateTime(2023, 10, 1, 0, 0)
        val endDate = DateTime(2024, 3, 1, 0, 0)
        val expected = listOf(
            DateTime(2023, 10, 1, 0, 0),
            DateTime(2023, 11, 1, 0, 0),
            DateTime(2023, 12, 1, 0, 0),
            DateTime(2024, 1, 1, 0, 0),
            DateTime(2024, 2, 1, 0, 0),
            DateTime(2024, 3, 1, 0, 0)
        )
        val result = generateMonthsRange(startDate, endDate)
        assertEquals(expected, result)
    }

    @Test(expected =  IllegalArgumentException::class)
    fun invalid_range_should_throw_exception() {
        val startDate = DateTime(2024, 5, 1, 0, 0)
        val endDate = DateTime(2024, 3, 1, 0, 0)
        generateMonthsRange(startDate, endDate)
    }

    @Test
    fun leap_year_february_should_be_handled_correctly() {
        val startDate = DateTime(2024, 2, 1, 0, 0)
        val endDate = DateTime(2024, 3, 1, 0, 0)
        val expected = listOf(
            DateTime(2024, 2, 1, 0, 0),
            DateTime(2024, 3, 1, 0, 0)
        )
        val result = generateMonthsRange(startDate, endDate)
        assertEquals(expected, result)
    }

    @Test
    fun start_date_not_on_first_of_month_should_normalize() {
        val startDate = DateTime(2024, 5, 15, 0, 0) // 15th May
        val endDate = DateTime(2024, 7, 1, 0, 0) // 1st July
        val expected = listOf(
            DateTime(2024, 5, 1, 0, 0), // Should normalize to May 1st
            DateTime(2024, 6, 1, 0, 0),
            DateTime(2024, 7, 1, 0, 0)
        )
        val result = generateMonthsRange(startDate, endDate)
        assertEquals(expected, result)
    }

    @Test
    fun end_date_not_on_first_of_month_should_still_include_correct_months() {
        val startDate = DateTime(2024, 5, 1, 0, 0)
        val endDate = DateTime(2024, 7, 25, 0, 0) // 25th July
        val expected = listOf(
            DateTime(2024, 5, 1, 0, 0),
            DateTime(2024, 6, 1, 0, 0),
            DateTime(2024, 7, 1, 0, 0) // Should still include July 1st
        )
        val result = generateMonthsRange(startDate, endDate)
        assertEquals(expected, result)
    }

    @Test
    fun very_large_range_should_perform_efficiently() {
        val startDate = DateTime(2000, 1, 1, 0, 0)
        val endDate = DateTime(2025, 12, 1, 0, 0)
        val expectedSize = (2025 - 2000 + 1) * 12 // 26 years * 12 months
        val result = generateMonthsRange(startDate, endDate)
        assertEquals(expectedSize, result.size)
    }

    @Test
    fun negative_years_should_be_handled_correctly() {
        val startDate = DateTime(-500, 1, 1, 0, 0) // Year -500
        val endDate = DateTime(-499, 12, 1, 0, 0) // Year -499
        val expected = (1..12).map { month -> DateTime(-500, month, 1, 0, 0) } +
                (1..12).map { month -> DateTime(-499, month, 1, 0, 0) }
        val result = generateMonthsRange(startDate, endDate)
        assertEquals(expected, result)
    }

}