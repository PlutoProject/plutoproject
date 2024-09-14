package ink.pmc.whitelist

import ink.pmc.utils.currentUnixTimestamp
import ink.pmc.whitelist.profile.ProfileFetcher
import kotlinx.serialization.SerialName
import java.util.*

fun createWhitelistModel(uuid: UUID, name: String, fetcher: ProfileFetcher): WhitelistModel {
    return WhitelistModel(uuid.toString(), name, currentUnixTimestamp, fetcher.id)
}

data class WhitelistModel(
    @SerialName("_id") val id: String,
    var rawName: String,
    val addedAt: Long,
    val fetcher: String
) {
    val name: String
        get() = rawName.lowercase()
}