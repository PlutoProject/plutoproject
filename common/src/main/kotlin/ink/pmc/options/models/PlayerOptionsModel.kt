package ink.pmc.options.models

import ink.pmc.options.api.PlayerOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal fun PlayerOptions.toModel(): PlayerOptionsModel {
    return PlayerOptionsModel(player.toString(), entries.map { it.toModel() })
}

@Serializable
data class PlayerOptionsModel(
    @SerialName("_id") val id: String,
    val entries: List<OptionEntryModel>
)