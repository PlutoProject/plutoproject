package ink.pmc.framework.playerdb

import ink.pmc.framework.playerdb.proto.PlayerDbRpc
import java.util.*

class ProxyDatabaseNotifier : DatabaseNotifier {
    override suspend fun notify(id: UUID) {
        PlayerDbRpc.notifyDatabaseUpdate(id)
    }
}