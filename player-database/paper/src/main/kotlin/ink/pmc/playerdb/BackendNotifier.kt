package ink.pmc.playerdb

import com.google.protobuf.Empty
import ink.pmc.playerdb.api.PlayerDb
import ink.pmc.playerdb.proto.PlayerDbRpcGrpcKt.PlayerDbRpcCoroutineStub
import ink.pmc.playerdb.proto.databaseIdentifier
import ink.pmc.rpc.api.RpcClient
import ink.pmc.utils.player.uuid
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.util.*

class BackendNotifier : Notifier, KoinComponent {

    override val id: UUID = UUID.randomUUID()
    private val stub = PlayerDbRpcCoroutineStub(RpcClient.channel)

    init {
        playerDbScope.launch {
            while (!disabled) {
                runCatching {
                    stub.monitorNotify(Empty.getDefaultInstance()).collect {
                        if (it.serverId.uuid == id) return@collect
                        val id = it.uuid.uuid
                        if (!PlayerDb.isLoaded(id)) return@collect
                        PlayerDb.reload(id)
                    }
                }
            }
        }
    }

    override suspend fun notify(id: UUID) {
        stub.notify(databaseIdentifier {
            serverId = id.toString()
            uuid = id.toString()
        })
    }

}