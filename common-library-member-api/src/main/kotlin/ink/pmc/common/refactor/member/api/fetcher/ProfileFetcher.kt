package ink.pmc.common.refactor.member.api.fetcher

import java.util.*

@Suppress("UNUSED")
interface ProfileFetcher {

    suspend fun fetch(name: String): UUID?

    suspend fun validate(name: String, uuid: UUID): Boolean

    suspend fun validate(name: String, uuid: String): Boolean

}