package ink.pmc.utils.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

fun ZoneId.toOffset(): ZoneOffset {
    return rules.getOffset(Instant.now())
}