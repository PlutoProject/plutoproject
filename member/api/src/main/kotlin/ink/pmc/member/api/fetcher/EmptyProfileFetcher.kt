package ink.pmc.member.api.fetcher

import java.util.*

@Suppress("UNUSED")
object EmptyProfileFetcher : AbstractProfileFetcher() {

    override suspend fun fetch(name: String): ProfileData {
        val uuid = UUID.nameUUIDFromBytes("OfflinePlayer:${name.lowercase()}".toByteArray(Charsets.UTF_8))
        return ProfileData(uuid, name)
    }
}