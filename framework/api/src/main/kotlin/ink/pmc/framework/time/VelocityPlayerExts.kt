package ink.pmc.framework.time

import com.velocitypowered.api.proxy.Player
import ink.pmc.framework.provider.Provider
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Suppress("UNUSED")
inline val Player.timezone: TimeZone
    get() = Provider.geoIpDatabase.tryCity(remoteAddress.address).getOrNull()?.location?.timeZone
        ?.let { TimeZone.getTimeZone(it) }
        ?: currentTimeZone