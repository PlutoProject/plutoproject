package ink.pmc.driftbottle

import ink.pmc.driftbottle.api.Bottle
import ink.pmc.driftbottle.api.BottleFactory
import ink.pmc.driftbottle.api.BottleState
import ink.pmc.driftbottle.models.BottleModel
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

class BottleFactoryImpl : BottleFactory {
    override fun create(creator: Player, content: List<String>): Bottle {
        return BottleImpl(
            BottleModel(
                id = UUID.randomUUID().toString(),
                creator = creator.uniqueId.toString(),
                createdAt = Instant.now().toEpochMilli(),
                content = content,
                state = BottleState.PICKED,
                operations = listOf()
            )
        )
    }
}