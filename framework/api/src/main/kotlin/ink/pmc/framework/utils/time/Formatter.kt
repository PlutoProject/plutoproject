package ink.pmc.framework.utils.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val morningFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd 上午 hh:mm:ss")
private val afternoonFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd 下午 hh:mm:ss")

fun LocalDateTime.format(): String {
    return if (hour < 12) morningFormatter.format(this) else afternoonFormatter.format(this)
}