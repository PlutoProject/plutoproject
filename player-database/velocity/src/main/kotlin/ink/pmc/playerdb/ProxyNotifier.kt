package ink.pmc.playerdb

import com.google.protobuf.Empty
import ink.pmc.playerdb.api.PlayerDb
import ink.pmc.playerdb.proto.PlayerDbRpcGrpcKt.PlayerDbRpcCoroutineImplBase
import ink.pmc.playerdb.proto.PlayerDbRpcOuterClass.DatabaseIdentifier
import ink.pmc.playerdb.proto.databaseIdentifier
import ink.pmc.utils.player.uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

class ProxyNotifier : Notifier, PlayerDbRpcCoroutineImplBase() {

    override val id: UUID = UUID.randomUUID()
    private val flow = MutableSharedFlow<DatabaseIdentifier>()
    private val empty = Empty.getDefaultInstance()

    override suspend fun notify(request: DatabaseIdentifier): Empty {
        val id = request.uuid.uuid
        if (PlayerDb.isLoaded(id)) {
            PlayerDb.reload(id)
        }
        notify(id)
        return empty
    }

    override fun monitorNotify(request: Empty): Flow<DatabaseIdentifier> {
        return flow
    }

    override suspend fun notify(id: UUID) {
        flow.emit(databaseIdentifier {
            serverId = id.toString()
            uuid = id.toString()
        })
    }

}