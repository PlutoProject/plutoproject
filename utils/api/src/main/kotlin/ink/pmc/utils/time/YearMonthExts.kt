package ink.pmc.utils.time

import java.time.LocalDate
import java.time.YearMonth

fun YearMonth.atStartOfMonth(): LocalDate {
    return atDay(1)
}