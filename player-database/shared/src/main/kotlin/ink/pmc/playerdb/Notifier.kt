package ink.pmc.playerdb

import java.util.UUID

interface Notifier {

    suspend fun notify(id: UUID)

}