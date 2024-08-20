package ink.pmc.utils.time

import java.time.ZoneId

inline val currentZoneId: ZoneId
    get() = ZoneId.systemDefault()