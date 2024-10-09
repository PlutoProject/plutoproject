package ink.pmc.options.proto

import com.google.protobuf.Empty
import ink.pmc.options.api.OptionsManager
import ink.pmc.options.proto.ContainerUpdateNotifyOuterClass.ContainerUpdateNotify
import ink.pmc.utils.concurrent.submitAsyncIO
import ink.pmc.utils.player.uuid
import ink.pmc.utils.proto.empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID

object OptionsRpc : OptionsRpcGrpcKt.OptionsRpcCoroutineImplBase() {
    private val id: UUID = UUID.randomUUID()
    private val broadcast = MutableSharedFlow<ContainerUpdateNotify>()

    override suspend fun notifyContainerUpdate(request: ContainerUpdateNotify): Empty {
        val player = request.player.uuid
        if (OptionsManager.isPlayerLoaded(player)) {
            OptionsManager.reloadOptions(player)
        }
        broadcast.emit(request)
        return empty
    }

    override fun monitorContainerUpdate(request: Empty): Flow<ContainerUpdateNotify> {
        return broadcast
    }

    fun notifyBackendContainerUpdate(player: UUID) {
        submitAsyncIO {
            broadcast.emit(containerUpdateNotify {
                serverId = id.toString()
                this.player = player.toString()
            })
        }
    }
}