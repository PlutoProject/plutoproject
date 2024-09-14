package ink.pmc.framework.playerdb

import java.util.*

interface DatabaseNotifier {
    suspend fun notify(id: UUID)
}