package ink.pmc.driftbottle

import ink.pmc.driftbottle.api.Bottle
import ink.pmc.driftbottle.api.BottleManager
import ink.pmc.driftbottle.repositories.BottleRepository
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class BottleManagerImpl : BottleManager, KoinComponent {
    private val repo by inject<BottleRepository>()

    override suspend fun get(id: UUID): Bottle? {
        return repo.findById(id)?.let { BottleImpl(it) }
    }

    override suspend fun getByCreator(player: Player): List<Bottle> {
        return repo.findByCreator(player.uniqueId).map { BottleImpl(it) }
    }

    override suspend fun random(): Bottle? {
        return repo.random()?.let { BottleImpl(it) }
    }

    override suspend fun delete(id: UUID) {
        repo.deleteById(id)
    }
}