package ink.pmc.framework.player.db

import ink.pmc.framework.inject.inlinedGet
import java.util.*

interface PlayerDb {

    companion object : PlayerDb by inlinedGet()

    fun isLoaded(id: UUID): Boolean

    fun getIfLoaded(id: UUID): Database?

    fun unload(id: UUID): Database?

    fun unloadAll()

    suspend fun reload(id: UUID): Database?

    suspend fun get(id: UUID): Database?

    suspend fun getOrCreate(id: UUID): Database

    suspend fun has(id: UUID): Boolean

    suspend fun create(id: UUID): Database

    suspend fun delete(id: UUID)

}