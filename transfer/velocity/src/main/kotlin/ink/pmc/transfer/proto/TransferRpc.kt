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
import ink.pmc.transfer.proto.TransferRspOuterClass.TransferRsp
import ink.pmc.transfer.proto.TransferRspOuterClass.TransferResult
import ink.pmc.transfer.proxy.AbstractProxyTransferService
import ink.pmc.utils.bedrock.uuid
import ink.pmc.utils.chat.json
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.multiplaform.player.velocity.wrapped
import ink.pmc.utils.platform.proxy
import kotlinx.coroutines.delay
import java.io.Closeable
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds

class TransferRpc(
    private val proxyServer: ProxyServer,
    private val service: AbstractProxyTransferService
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
        println("entry")
        val player = proxyServer.getPlayer(request.uuid.uuid).getOrNull() ?: return conditionVerifyRsp {
            println("null entry")
            result = ConditionVerifyResult.VERIFY_OFFLINE
        }
        println("block 1")

        val destination = service.getDestination(request.destination) ?: return conditionVerifyRsp {
            println("null block 2")
            result = ConditionVerifyResult.VERIFY_FAILED
        }
        println("block 2")

        return conditionVerifyRsp {
            println("returning")
            val verifyResult = service.conditionManager.verifyCondition(player.wrapped, destination)
            result = if (!verifyResult.first) {
                if (verifyResult.second != null) {
                    println("failed without msg")
                    errorMessage = verifyResult.second!!.json
                }
                println("failed")
                ConditionVerifyResult.VERIFY_FAILED
            } else {
                println("succeed")
                ConditionVerifyResult.VERIFY_SUCCEED
            }
        }
    }

    override suspend fun transferPlayer(request: TransferReq): TransferRsp {
        val player = proxy.getPlayer(request.uuid).getOrNull() ?: return transferRsp {
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

        if (healthyDestinations.contains(id)) {
            return empty
        }

        val destination = service.getDestination(id) as AbstractDestination? ?: return empty

        if (destination.status != DestinationStatus.OFFLINE) {
            return empty
        }

        destination.status = DestinationStatus.ONLINE
        destination.playerCount = playerCount
        destination.maxPlayerCount = maxPlayerCount
        healthyDestinations.add(id)

        return empty
    }

    override fun close() {
        closed = true
        healthyRefresh.cancel()
    }

}