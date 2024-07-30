package ink.pmc.essentials.dtos

import ink.pmc.essentials.api.home.Home
import ink.pmc.utils.storage.entity.LocationDto
import ink.pmc.utils.storage.entity.dto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

val Home.dto: HomeDto
    get() = HomeDto(
        id = id,
        name = name,
        createdAt = createdAt.toEpochMilli(),
        location = location.dto,
        owner = owner.uniqueId.toString()
    )

@Serializable
data class HomeDto(
    @Contextual val id: UUID,
    val name: String,
    val createdAt: Long,
    val location: LocationDto,
    val owner: String
)