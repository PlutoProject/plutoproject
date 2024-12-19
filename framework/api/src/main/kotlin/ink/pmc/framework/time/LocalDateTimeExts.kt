package ink.pmc.framework.time

import java.time.LocalDate
import java.time.LocalDateTime

@Suppress("NOTHING_TO_INLINE")
inline fun LocalDate.atEndOfDay(): LocalDateTime {
    return atTime(23, 59, 59)
}