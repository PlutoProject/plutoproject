package ink.pmc.transfer.proto

import com.google.protobuf.Empty
import com.google.protobuf.Value
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.transfer.AbstractTransferService
import ink.pmc.transfer.proto
import ink.pmc.transfer.proto.ConditionVerify.ConditionVerifyReq
import ink.pmc.transfer.proto.ConditionVerify.ConditionVerifyRsp
import ink.pmc.transfer.proto.ProtoCategoryOuterClass.ProtoCategory
import ink.pmc.transfer.proto.ProtoDestinationOuterClass.ProtoDestination
import ink.pmc.transfer.proto.SummaryOuterClass.Summary
import ink.pmc.transfer.proto.TransferRpcGrpcKt.TransferRpcCoroutineImplBase

class TransferRpc(
    private val proxyServer: ProxyServer,
    private val service: AbstractTransferService
) : TransferRpcCoroutineImplBase() {

    override suspend fun getSummary(request: Empty): Summary {
        return summary {
            onlinePlayers = proxyServer.playerCount
            destinations = destinationBundle { destinations.addAll(service.destinations.map { it.proto }) }
            categories = categoryBundle { categories.addAll(service.categories.map { it.proto }) }
        }
    }

    override suspend fun registerDestination(request: ProtoDestination): Empty {
        return super.registerDestination(request)
    }

    override suspend fun registryCategory(request: ProtoCategory): Empty {
        return super.registryCategory(request)
    }

    override suspend fun verifyCondition(request: ConditionVerifyReq): ConditionVerifyRsp {
        return super.verifyCondition(request)
    }

    override suspend fun reportHealthy(request: Value): Empty {
        return super.reportHealthy(request)
    }

}