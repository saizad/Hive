package com.hive.exts

import org.joda.time.DateTime
import java.text.NumberFormat
import java.util.Locale

fun Double.toAmount(locale: Locale = Locale("hi", "IN")): String {
    val format = NumberFormat.getCurrencyInstance(locale)
    return format.format(this).replace(".00", "")
}

fun Int.toAmount(locale: Locale = Locale("hi", "IN")): String {
    val format = NumberFormat.getCurrencyInstance(locale)
    return format.format(this).replace(".00", "")
}

fun Number.toReadableNumber(locale: Locale = Locale("hi", "IN")): String {
    return if (locale == Locale("hi", "IN") || locale == Locale("en", "IN")) {
        // Use custom Indian formatting for Indian locales
        this.toIndianReadableNumber()
    } else {
        // Use default locale-based formatting
        val format = NumberFormat.getNumberInstance(locale)
        format.format(this).replace(".00", "")
    }
}

fun Number.toIndianReadableNumber(): String {
    val numberString = this.toLong().toString()
    val decimalPart = if (this.toString().contains('.')) {
        this.toString().substringAfter('.')
    } else {
        ""
    }

    val result = StringBuilder()
    var count = 0

    // Handle the last 3 digits first (thousands place)
    for (i in numberString.length - 1 downTo 0) {
        result.append(numberString[i])
        count++
        if (i > 0 && ((count == 3) || (count == 2 && i <= numberString.length - 4))) {
            result.append(',')
            count = 0
        }
    }

    return result.reverse().toString() + if (decimalPart.isNotEmpty()) ".$decimalPart" else ""
}

val DateTime.greetings: String
    get() {
        return when (hourOfDay) {
            in 0..3 -> "Good Night"
            in 4..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

val greetings: String get() = DateTime.now().greetings
