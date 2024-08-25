package ink.pmc.utils.time

import java.time.ZoneId

@Suppress("UNUSED")
inline val currentZoneId: ZoneId
    get() = ZoneId.systemDefault()

val utcZoneId: ZoneId = ZoneId.of("UTC")