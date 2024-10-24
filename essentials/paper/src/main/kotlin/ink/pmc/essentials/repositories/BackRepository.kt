package ink.pmc.essentials.repositories

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.dtos.BackDto
import ink.pmc.provider.Provider
import ink.pmc.framework.utils.storage.model
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import org.bukkit.Location
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BackRepository : KoinComponent {

    private val conf by inject<EssentialsConfig>()
    private val options = ReplaceOptions().upsert(true)
    private val db =
        Provider.defaultMongoDatabase.getCollection<BackDto>("essentials_${conf.serverName}_backs")

    suspend fun find(player: Player): Location? {
        return findDto(player)?.location?.location
    }

    suspend fun findDto(player: Player): BackDto? {
        return db.find(eq("owner", player.uniqueId.toString())).firstOrNull()
    }

    suspend fun has(player: Player): Boolean {
        return find(player) != null
    }

    suspend fun save(player: Player, location: Location) {
        val existed = findDto(player)
        val dto = existed ?: BackDto(
            objectId = ObjectId(),
            owner = player.uniqueId,
            recordedAt = System.currentTimeMillis(),
            location = location.model
        )

        if (existed != null) {
            dto.recordedAt = System.currentTimeMillis()
            dto.location = location.model
        }

        db.replaceOne(eq("owner", player.uniqueId.toString()), dto, options)
    }

    suspend fun delete(player: Player) {
        db.deleteOne(eq("owner", player.uniqueId.toString()))
    }

}