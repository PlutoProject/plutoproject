package ink.pmc.common.refactor.member.api.fetcher

import java.util.*

@Suppress("UNUSED")
object EmptyProfileFetcher : AbstractProfileFetcher() {

    override suspend fun fetch(name: String): UUID? {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:${name.lowercase()}").toByteArray(Charsets.UTF_8))
    }


}