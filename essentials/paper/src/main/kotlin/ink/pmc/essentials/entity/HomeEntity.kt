package ink.pmc.essentials.entity

import ink.pmc.essentials.api.home.Home
import ink.pmc.utils.storage.entity.LocationEntity
import ink.pmc.utils.storage.entity.entity
import kotlinx.serialization.Serializable

val Home.entity: HomeEntity
    get() = HomeEntity(
        id = id.toString(),
        name = name,
        alias = alias,
        createdAt = createdAt.toEpochMilli(),
        location = location.entity,
        owner = owner.uniqueId.toString()
    )

@Serializable
data class HomeEntity(
    val id: String,
    val name: String,
    val alias: String?,
    val createdAt: Long,
    val location: LocationEntity,
    val owner: String
)