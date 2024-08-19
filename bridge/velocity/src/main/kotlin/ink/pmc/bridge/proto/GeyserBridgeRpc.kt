package ink.pmc.bridge.proto

import com.google.protobuf.Empty
import ink.pmc.bridge.proto.GeyserBridgeRpcGrpcKt.GeyserBridgeRpcCoroutineImplBase
import ink.pmc.bridge.proto.GeyserBridgeRpcOuterClass.PlayerIdentifier
import ink.pmc.utils.player.uuid
import org.geysermc.geyser.api.GeyserApi

class GeyserBridgeRpc : GeyserBridgeRpcCoroutineImplBase() {

    private val empty = Empty.getDefaultInstance()

    override suspend fun closeForm(request: PlayerIdentifier): Empty {
        val connection = GeyserApi.api().connectionByUuid(request.uuid.uuid) ?: return empty
        connection.closeForm()
        return empty
    }

}