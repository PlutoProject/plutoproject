package ink.pmc.framework.time

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val morningFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd 上午 hh:mm:ss")
private val afternoonFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd 下午 hh:mm:ss")
private val morningTimeFormatter = DateTimeFormatter.ofPattern("上午 hh:mm:ss")
private val afterTimeFormatter = DateTimeFormatter.ofPattern("下午 hh:mm:ss")
private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

fun LocalDateTime.format(): String {
    return if (hour < 12) morningFormatter.format(this) else afternoonFormatter.format(this)
}

fun LocalDateTime.formatTime(): String {
    return if (hour < 12) morningTimeFormatter.format(this) else afterTimeFormatter.format(this)
}

fun LocalDateTime.formatDate(): String {
    return dateFormatter.format(this)
}

fun ZonedDateTime.format(): String {
    return if (hour < 12) morningFormatter.format(this) else afternoonFormatter.format(this)
}

fun ZonedDateTime.formatTime(): String {
    return if (hour < 12) morningTimeFormatter.format(this) else afterTimeFormatter.format(this)
}

fun ZonedDateTime.formatDate(): String {
    return dateFormatter.format(this)
}

fun LocalDate.formatDate(): String {
    return dateFormatter.format(this)
}