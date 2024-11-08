package ink.pmc.framework.bridge

import com.google.protobuf.Empty
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineImplBase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.utils.proto.empty
import kotlinx.coroutines.flow.Flow

class BridgeRpc : BridgeRpcCoroutineImplBase() {
    override fun monitorNotification(request: Empty): Flow<Notification> {
        return super.monitorNotification(request)
    }

    override suspend fun registerServer(request: ServerInfo): ServerRegistrationAck {
        return super.registerServer(request)
    }

    override suspend fun heartbeat(request: ServerInfo): Empty {
        return empty
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        return super.operatePlayer(request)
    }

    override suspend fun ackPlayerOperation(request: PlayerOperationAck): PlayerOperationAck {
        return super.ackPlayerOperation(request)
    }
}