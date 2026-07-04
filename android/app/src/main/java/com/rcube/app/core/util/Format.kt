package com.rcube.app.core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Format paise as Indian-grouped rupees, e.g. 350000 -> "₹3,500", 1800000 -> "₹18,000". */
fun formatInr(paise: Long): String = "₹" + groupIndian(paise / 100)

/** Indian digit grouping (…,##,##,###). */
fun groupIndian(value: Long): String {
    val negative = value < 0
    val digits = kotlin.math.abs(value).toString()
    if (digits.length <= 3) return (if (negative) "-" else "") + digits
    val last3 = digits.takeLast(3)
    val rest = digits.dropLast(3)
    val sb = StringBuilder()
    var count = 0
    for (i in rest.indices.reversed()) {
        sb.append(rest[i])
        count++
        if (count % 2 == 0 && i != 0) sb.append(',')
    }
    val grouped = sb.reverse().toString()
    return (if (negative) "-" else "") + "$grouped,$last3"
}

private val dayMonthYear: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
private val dayMonth: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH)

fun LocalDate.formatLong(): String = format(dayMonthYear)
fun LocalDate.formatShort(): String = format(dayMonth)

/** Human relative label for a future date: "Today", "Tomorrow", "in 5 days". */
fun LocalDate.relativeLabel(today: LocalDate = LocalDate.now()): String {
    val days = ChronoUnit.DAYS.between(today, this)
    return when {
        days == 0L -> "Today"
        days == 1L -> "Tomorrow"
        days in 2..6 -> "in $days days"
        days < 0 -> "past"
        else -> formatShort()
    }
}

/** Compact "expires in Xh Ym" from remaining minutes. */
fun formatCountdown(totalMinutes: Long): String {
    if (totalMinutes <= 0) return "expired"
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return when {
        h >= 24 -> "${h / 24}d ${h % 24}h"
        h > 0 -> "${h}h ${m}m"
        else -> "${m}m"
    }
}

fun initialsOf(name: String): String =
    name.trim().split(" ").filter { it.isNotBlank() }
        .take(2).joinToString("") { it.first().uppercase() }
