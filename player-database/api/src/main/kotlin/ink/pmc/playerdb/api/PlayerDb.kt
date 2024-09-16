package ink.pmc.playerdb.api

import ink.pmc.utils.inject.inlinedGet
import java.util.UUID

interface PlayerDb {

    companion object : PlayerDb by inlinedGet()

    suspend fun get(id: UUID): Database?

    suspend fun getOrCreate(id: UUID): Database

    suspend fun has(id: UUID): Boolean

    suspend fun create(id: UUID): Database

    suspend fun remove(id: UUID)

    suspend fun clear(id: UUID)

}