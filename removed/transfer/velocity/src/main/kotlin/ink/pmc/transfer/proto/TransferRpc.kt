package ink.pmc.transfer.proto

import com.google.protobuf.Empty
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.transfer.AbstractDestination
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.proto
import ink.pmc.transfer.proto.ConditionVerify.*
import ink.pmc.transfer.proto.HealthyReportOuterClass.HealthyReport
import ink.pmc.transfer.proto.SummaryOuterClass.Summary
import ink.pmc.transfer.proto.TransferReqOuterClass.TransferReq
import ink.pmc.transfer.proto.TransferRpcGrpcKt.TransferRpcCoroutineImplBase
import ink.pmc.transfer.proto.TransferRspOuterClass.TransferResult
import ink.pmc.transfer.proto.TransferRspOuterClass.TransferRsp
import ink.pmc.transfer.proxy.AbstractProxyTransferService
import ink.pmc.framework.utils.chat.json
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.multiplaform.player.velocity.wrapped
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.framework.utils.player.uuid
import java.io.Closeable
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

class TransferRpc(
    private val proxyServer: ProxyServer,
    private val service: AbstractProxyTransferService
) : TransferRpcCoroutineImplBase(), Closeable {

    private var closed = false
    private val empty = Empty.getDefaultInstance()
    private val destinationReportTime = mutableMapOf<String, Instant>()
    private val healthyRefresh = submitAsync {
        while (!closed) {
            refreshHealthy()
        }
    }

    private fun refreshHealthy() {
        service.destinations.filter { destinationReportTime.containsKey(it.id) }.forEach {
            val time = destinationReportTime[it.id]!!.plusSeconds(10)
            if (time.isAfter(Instant.now()) || it.status != DestinationStatus.ONLINE) {
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
        val player = proxyServer.getPlayer(request.uuid.uuid).getOrNull() ?: return conditionVerifyRsp {
            result = ConditionVerifyResult.VERIFY_OFFLINE
        }

        val destination = service.getDestination(request.destination) ?: return conditionVerifyRsp {
            result = ConditionVerifyResult.VERIFY_FAILED
        }

        return conditionVerifyRsp {
            val verifyResult = service.conditionManager.verifyCondition(player.wrapped, destination)
            result = if (!verifyResult.first) {
                if (verifyResult.second != null) {
                    errorMessage = verifyResult.second!!.json
                }
                ConditionVerifyResult.VERIFY_FAILED
            } else {
                ConditionVerifyResult.VERIFY_SUCCEED
            }
        }
    }

    override suspend fun transferPlayer(request: TransferReq): TransferRsp {
        val player = proxy.getPlayer(request.uuid.uuid).getOrNull() ?: return transferRsp {
            result = TransferResult.TRANSFER_FAILED_PLAYER_OFFLINE
        }
        val destination = service.getDestination(request.destination) ?: return transferRsp {
            result = TransferResult.TRANSFER_FAILED_DEST_NOT_EXISTED
        }

        if (destination.status != DestinationStatus.ONLINE) {
            return transferRsp {
                result = TransferResult.TRANSFER_FAILED_DEST_OFFLINE
            }
        }

        val condition = service.conditionManager.verifyCondition(player.wrapped, destination).first

        if (!condition) {
            return transferRsp {
                result = TransferResult.TRANSFER_FAILED_CONDITION
            }
        }

        destination.transfer(player.wrapped)
        return transferRsp {
            result = TransferResult.TRANSFER_SUCCEED
        }
    }

    override suspend fun reportHealthy(request: HealthyReport): Empty {
        val id = request.id
        val playerCount = request.playerCount
        val maxPlayerCount = request.maxPlayerCount

        destinationReportTime[id] = Instant.now()
        val destination = service.getDestination(id) as AbstractDestination? ?: return empty

        if (destination.status != DestinationStatus.OFFLINE) {
            return empty
        }

        destination.status = DestinationStatus.ONLINE
        destination.playerCount = playerCount
        destination.maxPlayerCount = maxPlayerCount

        return empty
    }

    override fun close() {
        closed = true
        healthyRefresh.cancel()
    }

}