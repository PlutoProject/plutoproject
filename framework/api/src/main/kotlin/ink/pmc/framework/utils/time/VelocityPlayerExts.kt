package ink.pmc.framework.utils.time

import com.velocitypowered.api.proxy.Player
import ink.pmc.framework.provider.Provider
import java.util.*

@Suppress("UNUSED")
inline val Player.timezone: TimeZone
    get() = Provider.geoIpDatabase.city(remoteAddress.address)?.location?.timeZone
        ?.let { TimeZone.getTimeZone(it) }
        ?: currentTimeZone