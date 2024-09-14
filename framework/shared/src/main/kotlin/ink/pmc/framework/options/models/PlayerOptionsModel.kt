package ink.pmc.framework.options.models

import ink.pmc.framework.options.PlayerOptions
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