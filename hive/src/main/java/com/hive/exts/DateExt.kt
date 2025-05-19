package com.hive.exts

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Days
import org.joda.time.LocalDate


// Constants for date and time formats
const val DOB_DATE_FORMAT = "yyyy-MMM-dd"
const val APP_DATE_FORMAT = "yyyy-MM-dd"
const val APP_TIME_FORMAT = "h:mm:ss"
const val APP_TIME_FORMAT_24_HOURS = "H:mm:ss"


// Helper function to get ordinal suffix
fun ordinal(i: Int): String {
    val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when (i % 100) {
        11, 12, 13 -> "$i${suffixes[0]}"
        else -> "$i${suffixes[i % 10]}"
    }
}

// Extension properties for common date formats
val DateTime.dayMonthYear: String get() = toString("d MMMM, yyyy")
val DateTime.argsDate: String get() = toString(APP_DATE_FORMAT)
val DateTime.argsTime: String get() = toString("HH:mm")
val DateTime.timeStamp: String get() = toString("HH:mm a")

// Property to remove time zone
val DateTime.utcTime: DateTime get() = withZone(DateTimeZone.UTC)

val DateTime.noTimeZone: DateTime get() = withZone(DateTimeZone.forOffsetHoursMinutes(0, 0))

// Property for ordinal representation of date
val DateTime.ordinalYear: String
    get() = "${ordinal(dayOfMonth)} ${toString("MMMM yyyy")}"

val DateTime.ordinalYearShort: String
    get() = "${ordinal(dayOfMonth)} ${toString("MMM yy")}"

val DateTime.isToday: Boolean
    get() = withTimeAtStartOfDay().isEqual(DateTime.now().withTimeAtStartOfDay())

// Converts a time string (HH:mm:ss) to a DateTime object for today
val String.timeToToday: DateTime
    get() {
        val (hours, minutes, seconds) = split(":").map { it.toInt() }
        return DateTime.now().withTime(hours, minutes, seconds, 0)
    }

// Format example: "4th Feb '24"
val DateTime.formattedDateShort: String
    get() = "${ordinal(dayOfMonth)} ${toString("MMM")} '${toString("yy")}"

val DateTime.timeWithOrdinalDate: String
    get() = "${toString("hh:mm a").uppercase()} $ordinalYearShort"

/**
 * Generates a dynamic calendar with a configurable start day of the week and number of rows.
 *
 * @param year            The year for the calendar.
 * @param month           The month for the calendar.
 * @param startDayOfWeek  The starting day of the week (1 = Monday, 7 = Sunday).
 * @param numberOfRows    The number of rows in the calendar.
 */

fun generateCalendar(
    year: Int,
    month: Int,
    startDayOfWeek: Int,
    numberOfRows: Int
): List<LocalDate> {
    // Validate all input parameters
    require(year in 1900..9999) { "Year must be between 1900 and 9999, got: $year" }
    require(month in 1..12) { "Month must be between 1 and 12, got: $month" }
    require(startDayOfWeek in 1..7) { "Start day of week must be between 1 (Monday) and 7 (Sunday), got: $startDayOfWeek" }
    require(numberOfRows > 0) { "Number of rows must be positive, got: $numberOfRows" }

    val firstDay = LocalDate(year, month, 1)

    val startDate = firstDay.minusDays((firstDay.dayOfWeek - startDayOfWeek + 7) % 7)
    val endDate = startDate.plusDays(numberOfRows * 7 - 1)

    return generateSequence(startDate) { it.plusDays(1) }
        .takeWhile { it <= endDate }
        .toList()
}

// Check if two DateTime objects are in the same month
fun DateTime.isSameMonth(other: DateTime): Boolean =
    toString("yyyy-MM") == other.toString("yyyy-MM")

fun generateMonthsRange(startDate: DateTime = DateTime.now(), endDate: DateTime): List<DateTime> {
    require(!startDate.isAfter(endDate)) { "Start date must be before or equal to end date" }

    return generateSequence(startDate.withDayOfMonth(1)) { it.plusMonths(1) } // Normalize to first day
        .takeWhile { it <= endDate.withDayOfMonth(1) } // Ensure endDate is also treated as 1st
        .toList()
}

// Property to get the start of the month
val DateTime.startOfMonth: DateTime get() = dayOfMonth().withMinimumValue().withTimeAtStartOfDay()

val DateTime.daysBetween: Int
    get() {
        val today = DateTime.now().withTimeAtStartOfDay()
        val due = withTimeAtStartOfDay()
        return Days.daysBetween(today, due).days
    }
