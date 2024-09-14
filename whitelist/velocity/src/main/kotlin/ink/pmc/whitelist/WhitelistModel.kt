package ink.pmc.whitelist

import ink.pmc.utils.currentUnixTimestamp
import ink.pmc.whitelist.profile.ProfileFetcher
import java.util.*

fun UUID.createWhitelistModel(fetcher: ProfileFetcher): WhitelistModel {
    return WhitelistModel(this.toString(), currentUnixTimestamp, fetcher.id)
}

data class WhitelistModel(
    val uuid: String,
    val addedAt: Long,
    val authProvider: String
)