package ink.pmc.utils.time

import java.time.LocalDate
import java.time.LocalDateTime

fun LocalDate.atEndOfDay(): LocalDateTime {
    return atTime(23, 59, 59)
}