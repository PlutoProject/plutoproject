package ink.pmc.common.exchange.proto

import com.google.protobuf.Empty
import ink.pmc.common.exchange.proto.lobby2proxy.ItemDistributeNotifyOuterClass.ItemDistributeNotify
import ink.pmc.common.exchange.proto.lobby2proxy.LobbyHealthReportOuterClass.LobbyHealthReport
import ink.pmc.common.exchange.proto.proxy2lobby.LobbyHealthReportAckOuterClass.LobbyHealthReportAck
import ink.pmc.common.exchange.proto.proxy2lobby.lobbyHealthReportAck
import ink.pmc.common.exchange.proto.proxy2server.ExchangeCheckAckOuterClass.ExchangeCheckAck
import ink.pmc.common.exchange.proto.proxy2server.ExchangeEndAckOuterClass
import ink.pmc.common.exchange.proto.proxy2server.ExchangeEndAckOuterClass.ExchangeEndResult
import ink.pmc.common.exchange.proto.proxy2server.ExchangeStartAckOuterClass.ExchangeStartAck
import ink.pmc.common.exchange.proto.proxy2server.ExchangeStartAckOuterClass.ExchangeStartResult
import ink.pmc.common.exchange.proto.proxy2server.exchangeCheckAck
import ink.pmc.common.exchange.proto.proxy2server.exchangeEndAck
import ink.pmc.common.exchange.proto.proxy2server.exchangeStartAck
import ink.pmc.common.exchange.proto.server2lobby.ExchangeEndOuterClass.ExchangeEnd
import ink.pmc.common.exchange.proto.server2lobby.ExchangeStartOuterClass.ExchangeStart
import ink.pmc.common.exchange.proxy.AbstractProxyExchangeService
import ink.pmc.common.exchange.serverLogger
import ink.pmc.common.utils.platform.proxy
import ink.pmc.common.utils.proto.operation.ResultMessageOuterClass.ResultMessage
import ink.pmc.common.utils.proto.operation.ResultOuterClass
import ink.pmc.common.utils.proto.operation.resultMessage
import ink.pmc.common.utils.proto.player.PlayerOuterClass.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.Instant
import java.util.logging.Level
import kotlin.jvm.optionals.getOrNull

class ExchangeRpc(private val service: AbstractProxyExchangeService) :
    ExchangeRpcGrpcKt.ExchangeRpcCoroutineImplBase() {

    val itemDistributeFlow = MutableSharedFlow<ItemDistributeNotify>()

    override suspend fun startExchange(request: ExchangeStart): ExchangeStartAck {
        val player = proxy.getPlayer(request.player.uuid).getOrNull() ?: return exchangeStartAck {
            serviceId = service.id.toString()
            result = ExchangeStartResult.START_FAILED_OFFLINE
        }

        if (service.isInExchange(player)) {
            return exchangeStartAck {
                serviceId = service.id.toString()
                result = ExchangeStartResult.START_FAILED_ALREADY_IN
            }
        }

        service.startExchange(player)

        return exchangeStartAck {
            serviceId = service.id.toString()
            result = ExchangeStartResult.START_SUCCEED
        }
    }

    override suspend fun endExchange(request: ExchangeEnd): ExchangeEndAckOuterClass.ExchangeEndAck {
        val player = proxy.getPlayer(request.player.uuid).getOrNull() ?: return exchangeEndAck {
            serviceId = service.id.toString()
            result = ExchangeEndResult.END_FAILED_OFFLINE
        }

        if (!service.isInExchange(player)) {
            return exchangeEndAck {
                serviceId = service.id.toString()
                result = ExchangeEndResult.END_FAILED_NOT_IN
            }
        }

        service.endExchange(player)

        return exchangeEndAck {
            serviceId = service.id.toString()
            result = ExchangeEndResult.END_SUCCEED
        }
    }

    override suspend fun isInExchange(request: Player): ExchangeCheckAck {
        val player = proxy.getPlayer(request.uuid).getOrNull()
        return exchangeCheckAck {
            serviceId = service.id.toString()
            status = if (player == null) {
                ExchangeStatusOuterClass.ExchangeStatus.OFFLINE
            } else if (service.isInExchange(player)) {
                ExchangeStatusOuterClass.ExchangeStatus.IN_EXCHANGE
            } else {
                ExchangeStatusOuterClass.ExchangeStatus.NOT_IN_EXCHANGE
            }
        }
    }

    override suspend fun notifyItemDistribute(request: ItemDistributeNotify): ResultMessage {
        return try {
            itemDistributeFlow.emit(request)
            resultMessage { result = ResultOuterClass.Result.SUCCEED }
        } catch (e: Exception) {
            serverLogger.log(Level.SEVERE, "Failed to forward item distribute", e)
            resultMessage { result = ResultOuterClass.Result.FAILED }
        }
    }

    override suspend fun reportLobbyHealth(request: LobbyHealthReport): LobbyHealthReportAck {
        return lobbyHealthReportAck {
            serviceId = service.id.toString()
            time = Instant.now().toEpochMilli()
        }
    }

    override fun monitorItemDistribute(request: Empty): Flow<ItemDistributeNotify> {
        return itemDistributeFlow
    }

}