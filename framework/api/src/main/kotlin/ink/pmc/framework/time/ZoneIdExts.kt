package ink.pmc.framework.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@Suppress("NOTHING_TO_INLINE")
inline fun ZoneId.toOffset(): ZoneOffset {
    return rules.getOffset(Instant.now())
}