package ink.pmc.framework.playerdb.proto

import com.google.protobuf.Empty
import ink.pmc.framework.player.db.PlayerDb
import ink.pmc.framework.playerdb.proto.PlayerDbRpcGrpcKt.PlayerDbRpcCoroutineImplBase
import ink.pmc.framework.playerdb.proto.PlayerDbRpcOuterClass.DatabaseIdentifier
import ink.pmc.framework.concurrent.submitAsync
import ink.pmc.framework.player.uuid
import ink.pmc.framework.proto.empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent
import java.util.*

object PlayerDbRpc : PlayerDbRpcCoroutineImplBase(), KoinComponent {
    private val identity = UUID.randomUUID()
    private val broadcast = MutableSharedFlow<DatabaseIdentifier>()

    override suspend fun notify(request: DatabaseIdentifier): Empty {
        val id = request.uuid.uuid
        if (PlayerDb.isLoaded(id)) {
            PlayerDb.reload(id)
        }
        broadcast.emit(request)
        return empty
    }

    override fun monitorNotify(request: Empty): Flow<DatabaseIdentifier> {
        return broadcast
    }

    fun notifyDatabaseUpdate(player: UUID) {
        submitAsync {
            broadcast.emit(databaseIdentifier {
                serverId = identity.toString()
                uuid = player.toString()
            })
        }
    }
}