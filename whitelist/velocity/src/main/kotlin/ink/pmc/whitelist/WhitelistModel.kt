package ink.pmc.whitelist

import ink.pmc.utils.currentUnixTimestamp
import ink.pmc.whitelist.profile.ProfileFetcher
import kotlinx.serialization.SerialName
import java.util.*

fun createWhitelistModel(uuid: UUID, name: String, fetcher: ProfileFetcher): WhitelistModel {
    return WhitelistModel(uuid.toString(), name.lowercase(), name, currentUnixTimestamp, fetcher.id)
}

data class WhitelistModel(
    @SerialName("_id") val id: String,
    val name: String,
    val rawName: String,
    val addedAt: Long,
    val fetcher: String
)