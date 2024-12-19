package ink.pmc.framework.time

import ink.pmc.framework.provider.Provider
import org.bukkit.entity.Player
import java.time.ZoneId
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Suppress("UNUSED")
inline val Player.timezone: TimeZone
    get() = address?.let { address ->
        Provider.geoIpDatabase.tryCity(address.address).getOrNull()?.location?.timeZone
            ?.let { TimeZone.getTimeZone(it) }
    } ?: currentTimeZone

@Suppress("UNUSED")
inline val Player.zoneId: ZoneId
    get() = timezone.toZoneId()