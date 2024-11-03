package ink.pmc.framework.utils.time

import ink.pmc.framework.provider.Provider
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
inline val Player.timezone: TimeZone
    get() = address?.let { address ->
        Provider.geoIpDatabase.city(address.address)?.location?.timeZone
            ?.let { TimeZone.getTimeZone(it) }
    } ?: currentTimeZone