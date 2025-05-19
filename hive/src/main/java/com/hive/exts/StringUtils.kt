package com.hive.exts

import android.net.Uri
import android.webkit.URLUtil

fun String.name(firstName: Boolean = true): String {
    val split = this.trim().split("\\s+".toRegex()).toTypedArray()
    return if (!firstName) {
        split.filterIndexed { index, _ -> index >= 1 }.joinToString(separator = " ") { it }
    } else {
        split[0]
    }
}

val String.stripTrailingLeadingNewLines: String get() = this.trim('\n', '\r')

infix fun String?.emptyOrNull(value: String): String {
    if (isNullOrEmpty()) return value
    return this
}

val String?.toUri get() = if (this != null) Uri.parse(this) else null

fun Int.toQuantityString(singular: String, plural: String = singular + "s"): String {
    return "$this ${if (this == 1) singular else plural}"
}

/**
 * Masks a phone number, showing only the last 4 digits
 * Example: (123) 456-7890 becomes (***) ***-7890
 */
fun String?.maskPhoneNumber(mask: String = "*"): String? {
    val phoneNumber = this
    if (phoneNumber.isNullOrBlank()) return null

    // Remove all non-digit characters
    val digitsOnly = phoneNumber.filter { it.isDigit() }

    // Handle different phone number formats
    return when {
        // For standard 10-digit phone numbers
        digitsOnly.length >= 10 -> {
            val lastFour = digitsOnly.takeLast(4)
            "($mask$mask$mask) $mask$mask$mask-$lastFour"
        }
        // For shorter numbers, just mask all but last 2 digits
        digitsOnly.length > 2 -> {
            val lastTwo = digitsOnly.takeLast(2)
            "${"*".repeat(digitsOnly.length - 2)}$lastTwo"
        }
        // For very short numbers or invalid formats
        else -> "****"
    }
}

/**
 * Masks an email address showing only first character of local part
 * and the domain
 * Example: john.doe@example.com becomes j***@example.com
 */
fun String?.maskEmail(mask: String = "*"): String? {
    val email = this
    if (email.isNullOrBlank()) return null

    val parts = email.split("@")
    if (parts.size != 2) return "$mask$mask$mask$mask@$mask$mask$mask$mask.$mask$mask$mask"

    val localPart = parts[0]
    val domain = parts[1]

    // Show only first character of username + asterisks
    val maskedLocalPart = if (localPart.isNotEmpty()) {
        localPart.first() + "$mask".repeat(minOf(3, localPart.length - 1))
    } else {
        "$mask$mask$mask$mask"
    }

    return "$maskedLocalPart@$domain"
}

fun String?.toValidUriOrNull(): Uri? {
    val normalized = this?.trim()?.let {
        if (!it.startsWith("http://") && !it.startsWith("https://")) {
            "http://$it"
        } else it
    }

    return normalized?.takeIf {
        URLUtil.isValidUrl(it) &&
                Regex("""^https?://([\w-]+\.)+[\w-]{2,}(/.*)?$""").matches(it)
    }?.toUri
}

val Uri.domainName: String? get() {
    val host = host ?: return null
    val parts = host.split(".")
    return if (parts.size >= 2) parts[parts.size - 2] else null
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
