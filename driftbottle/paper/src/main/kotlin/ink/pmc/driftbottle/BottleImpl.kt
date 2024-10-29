package ink.pmc.driftbottle

import ink.pmc.driftbottle.api.Bottle
import ink.pmc.driftbottle.api.BottleOperation
import ink.pmc.driftbottle.api.BottleOperationType
import ink.pmc.driftbottle.api.BottleState
import ink.pmc.driftbottle.items.BottleItem
import ink.pmc.driftbottle.models.BottleModel
import ink.pmc.driftbottle.models.toModel
import ink.pmc.driftbottle.repositories.BottleRepository
import ink.pmc.framework.utils.player.uuid
import ink.pmc.framework.utils.time.instant
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.util.*

class BottleImpl(model: BottleModel) : Bottle, KoinComponent {
    private val repo by inject<BottleRepository>()
    override val id: UUID = model.id.uuid
    override val creator: UUID = model.creator.uuid
    override val createdAt: Instant = model.createdAt.instant
    override val content: List<String> = model.content
    override var state: BottleState = model.state
    override val operations: MutableList<BottleOperation> =
        model.operations.map { BottleOperationImpl(it) }.toMutableList()
    override val item: ItemStack
        get() = BottleItem(this)

    override fun logOperation(operator: Player, type: BottleOperationType) {
        operations.add(
            BottleOperationImpl(
                Instant.now(),
                operator.uniqueId,
                type
            )
        )
    }

    override suspend fun saveOrUpdate() {
        repo.saveOrUpdate(toModel())
    }
}