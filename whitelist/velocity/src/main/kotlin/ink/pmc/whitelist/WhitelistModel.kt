package ink.pmc.whitelist

import ink.pmc.framework.utils.currentUnixTimestamp
import ink.pmc.whitelist.profile.ProfileFetcher
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

fun createWhitelistModel(uuid: UUID, name: String, fetcher: ProfileFetcher): WhitelistModel {
    return WhitelistModel(uuid.toString(), name, currentUnixTimestamp, fetcher.id)
}

@Serializable
data class WhitelistModel(
    @SerialName("_id") val id: String,
    var rawName: String,
    val addedAt: Long,
    val fetcher: String
) {
    val name: String = rawName.lowercase()
}