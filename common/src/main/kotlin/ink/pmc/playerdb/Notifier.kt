package ink.pmc.playerdb

import java.util.UUID

interface Notifier {

    val id: UUID

    suspend fun notify(id: UUID)

}