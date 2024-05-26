package ink.pmc.member.api.fetcher

import java.util.*

@Suppress("UNUSED")
interface ProfileFetcher {

    suspend fun fetch(name: String): ProfileData?

    suspend fun validate(name: String, uuid: UUID): Boolean

    suspend fun validate(name: String, uuid: String): Boolean

}