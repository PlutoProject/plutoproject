package ink.pmc.whitelist.profile

import java.util.*

@Suppress("UNUSED")
object OfflineProfileFetcher : AbstractProfileFetcher() {

    override val id: String = "offline"

    override suspend fun fetch(name: String): ProfileData {
        val uuid = UUID.nameUUIDFromBytes("OfflinePlayer:${name.lowercase()}".toByteArray(Charsets.UTF_8))
        return ProfileData(uuid, name)
    }

}