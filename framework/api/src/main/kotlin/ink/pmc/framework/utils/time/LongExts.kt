package ink.pmc.framework.utils.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

inline val Long.instant: Instant
    get() = Instant.ofEpochMilli(this)

@Suppress("NOTHING_TO_INLINE")
inline fun Long.toLocalDateTime(zone: ZoneId): LocalDateTime {
    return LocalDateTime.ofInstant(instant, zone)
}