package ink.pmc.framework.playerdb

import java.util.*

interface Notifier {
    val id: UUID

    suspend fun notify(id: UUID)
}