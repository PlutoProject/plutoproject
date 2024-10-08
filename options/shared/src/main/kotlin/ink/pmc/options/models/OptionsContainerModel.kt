package ink.pmc.options.models

import ink.pmc.options.api.OptionsContainer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal fun OptionsContainer.toModel(): OptionsContainerModel {
    return OptionsContainerModel(owner.toString(), entries.map { it.toModel() })
}

@Serializable
data class OptionsContainerModel(
    @SerialName("_id") val id: String,
    val entries: List<OptionEntryModel>
)