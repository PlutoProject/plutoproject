package ink.pmc.framework.options.proto

import com.google.protobuf.Empty
import ink.pmc.framework.options.OptionsManager
import ink.pmc.framework.options.proto.OptionsRpcGrpcKt.OptionsRpcCoroutineImplBase
import ink.pmc.framework.options.proto.OptionsUpdateNotifyOuterClass.OptionsUpdateNotify
import ink.pmc.framework.concurrent.submitAsyncIO
import ink.pmc.framework.player.uuid
import ink.pmc.framework.proto.empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

object OptionsRpc : OptionsRpcCoroutineImplBase() {
    private val id: UUID = UUID.randomUUID()
    private val broadcast = MutableSharedFlow<OptionsUpdateNotify>()

    override suspend fun notifyOptionsUpdate(request: OptionsUpdateNotify): Empty {
        val player = request.player.uuid
        if (OptionsManager.isPlayerLoaded(player)) {
            OptionsManager.reloadOptions(player)
        }
        broadcast.emit(request)
        return empty
    }

    override fun monitorOptionsUpdate(request: Empty): Flow<OptionsUpdateNotify> {
        return broadcast
    }

    fun notifyBackendContainerUpdate(player: UUID) {
        submitAsyncIO {
            broadcast.emit(optionsUpdateNotify {
                serverId = id.toString()
                this.player = player.toString()
            })
        }
    }
}