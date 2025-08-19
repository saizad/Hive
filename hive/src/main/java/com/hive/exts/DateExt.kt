package com.hive.exts

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.LocalDate
import org.joda.time.Minutes
import org.joda.time.Months
import org.joda.time.Seconds
import org.joda.time.Weeks
import org.joda.time.Years
import kotlin.math.abs

// Constants for date and time formats
const val DOB_DATE_FORMAT = "yyyy-MMM-dd"
const val APP_DATE_FORMAT = "yyyy-MM-dd"
const val APP_TIME_FORMAT = "h:mm:ss"
const val APP_TIME_FORMAT_24_HOURS = "H:mm:ss"
const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val FULL_DATE_TIME_FORMAT = "EEEE, MMMM dd, yyyy 'at' HH:mm"
const val COMPACT_DATE_FORMAT = "dd/MM/yy"
const val US_DATE_FORMAT = "MM/dd/yyyy"

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

val DateTime.isYesterday: Boolean
    get() = withTimeAtStartOfDay().isEqual(DateTime.now().minusDays(1).withTimeAtStartOfDay())

val DateTime.isTomorrow: Boolean
    get() = withTimeAtStartOfDay().isEqual(DateTime.now().plusDays(1).withTimeAtStartOfDay())

val DateTime.isThisWeek: Boolean
    get() {
        val now = DateTime.now()
        val weekStart = now.withDayOfWeek(1).withTimeAtStartOfDay()
        val weekEnd = now.withDayOfWeek(7).withTime(23, 59, 59, 999)
        return isAfter(weekStart.minusMillis(1)) && isBefore(weekEnd.plusMillis(1))
    }

val DateTime.isThisMonth: Boolean
    get() = isSameMonth(DateTime.now())

val DateTime.isThisYear: Boolean
    get() = year == DateTime.now().year

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

// Social Media Style "Ago" Extensions
val DateTime.timeAgo: String
    get() {
        val now = DateTime.now()
        val diffInSeconds = Seconds.secondsBetween(this, now).seconds.toLong()

        return when {
            diffInSeconds < 0 -> "in the future"
            diffInSeconds < 60 -> "just now"
            diffInSeconds < 3600 -> "${diffInSeconds / 60}m"
            diffInSeconds < 86400 -> "${diffInSeconds / 3600}h"
            diffInSeconds < 604800 -> "${diffInSeconds / 86400}d"
            diffInSeconds < 2592000 -> "${diffInSeconds / 604800}w"
            diffInSeconds < 31536000 -> "${diffInSeconds / 2592000}mo"
            else -> "${diffInSeconds / 31536000}y"
        }
    }

val DateTime.timeAgoDetailed: String
    get() {
        val now = DateTime.now()
        val diffInSeconds = Seconds.secondsBetween(this, now).seconds.toLong()

        return when {
            diffInSeconds < 0 -> "in the future"
            diffInSeconds < 60 -> "just now"
            diffInSeconds < 120 -> "1 minute ago"
            diffInSeconds < 3600 -> "${diffInSeconds / 60} minutes ago"
            diffInSeconds < 7200 -> "1 hour ago"
            diffInSeconds < 86400 -> "${diffInSeconds / 3600} hours ago"
            diffInSeconds < 172800 -> "yesterday"
            diffInSeconds < 604800 -> "${diffInSeconds / 86400} days ago"
            diffInSeconds < 1209600 -> "1 week ago"
            diffInSeconds < 2592000 -> "${diffInSeconds / 604800} weeks ago"
            diffInSeconds < 5184000 -> "1 month ago"
            diffInSeconds < 31536000 -> "${diffInSeconds / 2592000} months ago"
            else -> "${diffInSeconds / 31536000} years ago"
        }
    }

// WhatsApp/Telegram style timestamp
val DateTime.chatTimeStamp: String
    get() = when {
        isToday -> toString("HH:mm")
        isYesterday -> "Yesterday"
        isThisWeek -> toString("EEEE")
        isThisYear -> toString("dd/MM")
        else -> toString("dd/MM/yy")
    }

// Twitter style timestamp
val DateTime.twitterTimeStamp: String
    get() {
        val now = DateTime.now()
        val hours = Hours.hoursBetween(this, now).hours
        val days = Days.daysBetween(this, now).days

        return when {
            hours < 24 -> timeAgo
            days < 7 -> toString("MMM d")
            isThisYear -> toString("MMM d")
            else -> toString("MMM d, yyyy")
        }
    }

// Additional useful extensions
val DateTime.weekday: String get() = toString("EEEE")
val DateTime.weekdayShort: String get() = toString("EEE")
val DateTime.monthName: String get() = toString("MMMM")
val DateTime.monthNameShort: String get() = toString("MMM")

val DateTime.quarter: Int
    get() = when (monthOfYear) {
        in 1..3 -> 1
        in 4..6 -> 2
        in 7..9 -> 3
        else -> 4
    }

val DateTime.quarterName: String get() = "Q$quarter ${year}"

val DateTime.weekOfYear: Int get() = weekOfWeekyear

val DateTime.isWeekend: Boolean
    get() = dayOfWeek == 6 || dayOfWeek == 7

val DateTime.isWorkday: Boolean get() = !isWeekend

// Age calculation
fun DateTime.ageInYears(birthDate: DateTime): Int =
    Years.yearsBetween(birthDate, this).years

fun DateTime.ageInMonths(birthDate: DateTime): Int =
    Months.monthsBetween(birthDate, this).months

fun DateTime.ageInDays(birthDate: DateTime): Int =
    Days.daysBetween(birthDate, this).days

// Start and end of periods
val DateTime.startOfWeek: DateTime
    get() = withDayOfWeek(1).withTimeAtStartOfDay()

val DateTime.endOfWeek: DateTime
    get() = withDayOfWeek(7).withTime(23, 59, 59, 999)

val DateTime.startOfMonth: DateTime
    get() = dayOfMonth().withMinimumValue().withTimeAtStartOfDay()

val DateTime.endOfMonth: DateTime
    get() = dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999)

val DateTime.startOfYear: DateTime
    get() = withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay()

val DateTime.endOfYear: DateTime
    get() = withMonthOfYear(12).withDayOfMonth(31).withTime(23, 59, 59, 999)

val DateTime.startOfDay: DateTime get() = withTimeAtStartOfDay()
val DateTime.endOfDay: DateTime get() = withTime(23, 59, 59, 999)

// Duration between dates
val DateTime.daysBetween: Int
    get() {
        val today = DateTime.now().withTimeAtStartOfDay()
        val due = withTimeAtStartOfDay()
        return Days.daysBetween(today, due).days
    }

fun DateTime.daysBetween(other: DateTime): Int =
    Days.daysBetween(this.withTimeAtStartOfDay(), other.withTimeAtStartOfDay()).days

fun DateTime.hoursBetween(other: DateTime): Int =
    Hours.hoursBetween(this, other).hours

fun DateTime.minutesBetween(other: DateTime): Int =
    Minutes.minutesBetween(this, other).minutes

fun DateTime.weeksBetween(other: DateTime): Int =
    Weeks.weeksBetween(this.withTimeAtStartOfDay(), other.withTimeAtStartOfDay()).weeks

// Formatting extensions
val DateTime.iso8601: String get() = toString(ISO_FORMAT)
val DateTime.compactDate: String get() = toString(COMPACT_DATE_FORMAT)
val DateTime.usDate: String get() = toString(US_DATE_FORMAT)
val DateTime.fullDateTime: String get() = toString(FULL_DATE_TIME_FORMAT)

// Relative date descriptions
val DateTime.relativeDate: String
    get() = when {
        isToday -> "Today"
        isYesterday -> "Yesterday"
        isTomorrow -> "Tomorrow"
        daysBetween == -2 -> "Day after tomorrow"
        daysBetween == 2 -> "Day before yesterday"
        daysBetween in -7..-3 -> "This ${toString("EEEE")}"
        daysBetween in 3..7 -> "Last ${toString("EEEE")}"
        isThisWeek -> toString("EEEE")
        daysBetween in -14..-8 -> "Next week"
        daysBetween in 8..14 -> "Last week"
        isThisMonth -> "${abs(daysBetween)} days ${if (daysBetween < 0) "from now" else "ago"}"
        isThisYear -> toString("MMMM d")
        else -> toString("MMMM d, yyyy")
    }

// Smart formatting that adapts based on time difference
val DateTime.smartFormat: String
    get() {
        val now = DateTime.now()
        val daysDiff = abs(Days.daysBetween(this, now).days)

        return when {
            daysDiff == 0 -> toString("HH:mm")
            daysDiff < 7 -> relativeDate
            daysDiff < 365 -> toString("MMM d")
            else -> toString("MMM d, yyyy")
        }
    }

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

// Date range generators
fun generateDateRange(startDate: DateTime, endDate: DateTime): List<DateTime> {
    require(!startDate.isAfter(endDate)) { "Start date must be before or equal to end date" }

    return generateSequence(startDate.withTimeAtStartOfDay()) { it.plusDays(1) }
        .takeWhile { it <= endDate.withTimeAtStartOfDay() }
        .toList()
}

fun generateWeekRange(startDate: DateTime, endDate: DateTime): List<DateTime> {
    require(!startDate.isAfter(endDate)) { "Start date must be before or equal to end date" }

    return generateSequence(startDate.startOfWeek) { it.plusWeeks(1) }
        .takeWhile { it <= endDate.startOfWeek }
        .toList()
}

// Time zone utilities
fun DateTime.toTimeZone(timeZone: DateTimeZone): DateTime = withZone(timeZone)
fun DateTime.toLocalTime(): DateTime = withZone(DateTimeZone.getDefault())

// Validation extensions
val DateTime.isPast: Boolean get() = isBefore(DateTime.now())
val DateTime.isFuture: Boolean get() = isAfter(DateTime.now())

// Business day calculations
fun DateTime.addBusinessDays(days: Int): DateTime {
    var result = this
    var remainingDays = days

    while (remainingDays > 0) {
        result = result.plusDays(1)
        if (!result.isWeekend) {
            remainingDays--
        }
    }

    return result
}

fun DateTime.businessDaysBetween(other: DateTime): Int {
    val start = if (isBefore(other)) this else other
    val end = if (isBefore(other)) other else this

    var businessDays = 0
    var current = start.withTimeAtStartOfDay()

    while (current.isBefore(end.withTimeAtStartOfDay()) || current.isEqual(end.withTimeAtStartOfDay())) {
        if (!current.isWeekend) {
            businessDays++
        }
        current = current.plusDays(1)
    }

    return businessDays
}