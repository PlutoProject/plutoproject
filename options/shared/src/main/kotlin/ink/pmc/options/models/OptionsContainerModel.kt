package ink.pmc.options.models

import ink.pmc.options.api.PlayerOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal fun PlayerOptions.toModel(): OptionsContainerModel {
    return OptionsContainerModel(player.toString(), entries.map { it.toModel() })
}

@Serializable
data class OptionsContainerModel(
    @SerialName("_id") val id: String,
    val entries: List<OptionEntryModel>
)