package ink.pmc.driftbottle.api

import ink.pmc.framework.utils.inject.inlinedGet
import java.util.UUID

interface BottleManager {
    companion object : BottleManager by inlinedGet()

    suspend fun getBottle(id: UUID): Bottle?
}