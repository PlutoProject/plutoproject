package ink.pmc.common.utils

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

val Date.unixTimestamp: Long
    get() = Date().toInstant().epochSecond

val Date.localeDate: String
    get() {
        val timeDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
        val formattedDateTime = timeDate.format(formatter)

        return formattedDateTime
    }