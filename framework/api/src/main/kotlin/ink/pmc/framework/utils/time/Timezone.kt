package ink.pmc.framework.utils.time

import java.time.ZoneId

@Suppress("UNUSED")
inline val currentZoneId: ZoneId
    get() = ZoneId.systemDefault()