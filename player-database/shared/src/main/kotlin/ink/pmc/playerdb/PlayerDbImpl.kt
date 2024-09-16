package ink.pmc.playerdb

import ink.pmc.playerdb.api.Database
import ink.pmc.playerdb.api.PlayerDb
import java.util.*

class PlayerDbImpl : PlayerDb {

    override suspend fun get(id: UUID): Database? {
        TODO("Not yet implemented")
    }

    override suspend fun getOrCreate(id: UUID): Database {
        TODO("Not yet implemented")
    }

    override suspend fun has(id: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun create(id: UUID): Database {
        TODO("Not yet implemented")
    }

    override suspend fun remove(id: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun clear(id: UUID) {
        TODO("Not yet implemented")
    }

}