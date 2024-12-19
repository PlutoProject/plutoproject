package ink.pmc.framework.player.profile

import ink.pmc.framework.inject.inlinedGet
import java.util.*

interface ProfileCache {
    companion object : ProfileCache by inlinedGet()

    suspend fun getByName(name: String): CachedProfile?

    suspend fun getByUuid(uuid: UUID): CachedProfile?
}