package ink.pmc.common.refactor.member.api.data

import java.util.*

@Suppress("UNUSED")
interface DataRepository {

    suspend fun registerEntry(owner: UUID): DataEntry

    suspend fun getById(id: UUID): DataEntry?

    suspend fun getByOwner(owner: UUID): DataEntry?

    suspend fun update(entry: DataEntry)

    suspend fun refresh(entry: DataEntry)

}