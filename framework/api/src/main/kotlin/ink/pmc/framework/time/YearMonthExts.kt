package ink.pmc.framework.time

import java.time.LocalDate
import java.time.YearMonth

@Suppress("NOTHING_TO_INLINE")
inline fun YearMonth.atStartOfMonth(): LocalDate {
    return atDay(1)
}