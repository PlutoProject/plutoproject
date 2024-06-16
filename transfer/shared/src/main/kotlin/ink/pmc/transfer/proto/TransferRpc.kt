package ink.pmc.transfer.proto

import com.google.protobuf.Empty
import com.google.protobuf.Value
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.transfer.AbstractDestination
import ink.pmc.transfer.AbstractTransferService
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.proto
import ink.pmc.transfer.proto.ConditionVerify.*
import ink.pmc.transfer.proto.SummaryOuterClass.Summary
import ink.pmc.transfer.proto.TransferRpcGrpcKt.TransferRpcCoroutineImplBase
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.multiplaform.player.velocity.wrapped
import kotlinx.coroutines.delay
import java.io.Closeable
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds

class TransferRpc(
    private val proxyServer: ProxyServer,
    private val service: AbstractTransferService
) : TransferRpcCoroutineImplBase(), Closeable {

    private var closed = false
    private val empty = Empty.getDefaultInstance()
    private val healthyDestinations = mutableSetOf<String>()
    private val healthyRefresh = submitAsync {
        while (!closed) {
            delay(10.seconds)
            refreshHealthy()
        }
    }

    private fun refreshHealthy() {
        service.destinations.filter { !healthyDestinations.contains(it.id) }.forEach {
            if (it.status != DestinationStatus.ONLINE) {
                return@forEach
            }

            it.status = DestinationStatus.OFFLINE
        }
    }

    override suspend fun getSummary(request: Empty): Summary {
        return summary {
            onlinePlayers = proxyServer.playerCount
            destinations = destinationBundle { destinations.addAll(service.destinations.map { it.proto }) }
            categories = categoryBundle { categories.addAll(service.categories.map { it.proto }) }
        }
    }

    override suspend fun verifyCondition(request: ConditionVerifyReq): ConditionVerifyRsp {
        val player = proxyServer.getPlayer(request.uuid).getOrNull() ?: return conditionVerifyRsp {
            result = ConditionVerifyResult.VERIFY_OFFLINE
        }

        val destination = service.getDestination(request.destination) ?: return conditionVerifyRsp {
            result = ConditionVerifyResult.VERIFY_FAILED
        }

        return conditionVerifyRsp {
            result = if (!destination.condition.invoke(player.wrapped)) {
                ConditionVerifyResult.VERIFY_FAILED
            } else {
                ConditionVerifyResult.VERIFY_SUCCEED
            }
        }
    }

    override suspend fun reportHealthy(request: Value): Empty {
        val id = request.stringValue

        if (healthyDestinations.contains(id)) {
            return empty
        }

        val destination = service.getDestination(id) as AbstractDestination? ?: return empty

        if (destination.status != DestinationStatus.OFFLINE) {
            return empty
        }

        destination.status = DestinationStatus.ONLINE
        healthyDestinations.add(id)
        return empty
    }

    override fun close() {
        closed = true
        healthyRefresh.cancel()
    }

}