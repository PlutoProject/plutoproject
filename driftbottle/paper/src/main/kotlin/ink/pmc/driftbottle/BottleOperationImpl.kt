package ink.pmc.driftbottle

import ink.pmc.driftbottle.api.BottleOperation
import ink.pmc.driftbottle.api.BottleOperationType
import ink.pmc.driftbottle.models.BottleOperationModel
import ink.pmc.framework.utils.player.uuid
import ink.pmc.framework.utils.time.instant
import java.time.Instant
import java.util.*

class BottleOperationImpl(
    override val time: Instant,
    override val operator: UUID,
    override val type: BottleOperationType
) : BottleOperation {
    constructor(model: BottleOperationModel) : this(model.time.instant, model.operator.uuid, model.type)
}