package ink.pmc.essentials.dtos

import ink.pmc.essentials.api.warp.Warp
import ink.pmc.utils.storage.entity.LocationDto
import ink.pmc.utils.storage.entity.dto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

val Warp.dto: WarpDto
    get() = WarpDto(
        id = id,
        name = name,
        alias = alias,
        createdAt = createdAt.toEpochMilli(),
        location = location.dto,
    )

@Serializable
data class WarpDto(
    @Contextual val id: UUID,
    val name: String,
    val alias: String?,
    val createdAt: Long,
    val location: LocationDto,
)