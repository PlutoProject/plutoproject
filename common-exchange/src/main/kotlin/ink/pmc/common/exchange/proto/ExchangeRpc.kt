package ink.pmc.common.exchange.proto

import com.google.protobuf.Empty
import ink.pmc.common.exchange.AbstractProxyExchangeService
import ink.pmc.common.exchange.proto.lobby2proxy.ItemDistributeNotifyOuterClass.ItemDistributeNotify
import ink.pmc.common.exchange.proto.lobby2proxy.LobbyHealthReportOuterClass.LobbyHealthReport
import ink.pmc.common.exchange.proto.proxy2lobby.LobbyHealthReportAckOuterClass.LobbyHealthReportAck
import ink.pmc.common.exchange.proto.proxy2server.ExchangeCheckAckOuterClass.ExchangeCheckAck
import ink.pmc.common.exchange.proto.proxy2server.ExchangeStartAckOuterClass.ExchangeStartAck
import ink.pmc.common.exchange.proto.server2lobby.ExchangeEndOuterClass.ExchangeEnd
import ink.pmc.common.exchange.proto.server2lobby.ExchangeStartOuterClass.ExchangeStart
import ink.pmc.common.utils.proto.operation.ResultMessageOuterClass.ResultMessage
import ink.pmc.common.utils.proto.player.PlayerOuterClass.Player
import ink.pmc.common.utils.proto.velocity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class ExchangeRpc(val service: AbstractProxyExchangeService) : ExchangeRpcGrpcKt.ExchangeRpcCoroutineImplBase() {

    val itemDistributeFlow = MutableSharedFlow<ItemDistributeNotify>()

    override suspend fun startExchange(request: ExchangeStart): ExchangeStartAck {
        val player = request.player.velocity
        TODO()
    }

    override suspend fun endExchange(request: ExchangeEnd): ResultMessage {
        return super.endExchange(request)
    }

    override suspend fun isInExchange(request: Player): ExchangeCheckAck {
        return super.isInExchange(request)
    }

    override suspend fun notifyItemDistribute(request: ItemDistributeNotify): ResultMessage {
        return super.notifyItemDistribute(request)
    }

    override suspend fun reportLobbyHealth(request: LobbyHealthReport): LobbyHealthReportAck {
        return super.reportLobbyHealth(request)
    }

    override fun monitorItemDistribute(request: Empty): Flow<ItemDistributeNotify> {
        return itemDistributeFlow
    }

}