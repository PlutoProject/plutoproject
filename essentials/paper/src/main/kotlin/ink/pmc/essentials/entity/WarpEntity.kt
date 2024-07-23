package ink.pmc.essentials.entity

import ink.pmc.essentials.api.warp.Warp
import ink.pmc.utils.storage.entity.LocationEntity
import ink.pmc.utils.storage.entity.entity
import kotlinx.serialization.Serializable

val Warp.entity: WarpEntity
    get() = WarpEntity(
        name = name,
        alias = alias,
        createdAt = createdAt.toEpochMilli(),
        location = location.entity,
        cost = cost.toString()
    )

@Serializable
data class WarpEntity(
    val name: String,
    val alias: String?,
    val createdAt: Long,
    val location: LocationEntity,
    val cost: String
)