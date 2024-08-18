package ink.pmc.utils.time

import java.time.ZoneId

val currentZoneId: ZoneId
    get() = ZoneId.systemDefault()