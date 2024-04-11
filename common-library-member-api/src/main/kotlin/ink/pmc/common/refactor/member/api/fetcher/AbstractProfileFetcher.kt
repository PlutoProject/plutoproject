package ink.pmc.common.refactor.member.api.fetcher

import okhttp3.OkHttpClient
import java.util.*

@Suppress("UNUSED")
abstract class AbstractProfileFetcher() : ProfileFetcher {

    internal val httpClient = OkHttpClient()

    override suspend fun validate(name: String, uuid: UUID): Boolean {
        val fetchedId = fetch(name)
        return !(fetchedId == null || fetchedId != uuid)
    }

    override suspend fun validate(name: String, uuid: String): Boolean = validate(name, UUID.fromString(uuid))

}