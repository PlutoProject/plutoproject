package ink.pmc.framework.utils.time

import java.time.ZoneId
import java.util.*

@Suppress("UNUSED")
inline val currentZoneId: ZoneId
    get() = ZoneId.systemDefault()

inline val currentTimeZone: TimeZone
    get() = TimeZone.getTimeZone(currentZoneId)