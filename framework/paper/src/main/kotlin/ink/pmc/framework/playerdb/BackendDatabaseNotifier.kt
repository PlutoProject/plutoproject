package ink.pmc.framework.playerdb

import org.koin.core.component.KoinComponent
import java.util.*

class BackendDatabaseNotifier : DatabaseNotifier, KoinComponent {
    override suspend fun notify(id: UUID) {
        sendUpdateNotification(id)
    }
}