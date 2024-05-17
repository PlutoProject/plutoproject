package ink.pmc.common.exchange.proto

import com.google.protobuf.Empty
import ink.pmc.common.exchange.proto.lobby2proxy.ItemDistributeNotifyOuterClass
import ink.pmc.common.exchange.proto.lobby2proxy.LobbyHealthReportOuterClass
import ink.pmc.common.exchange.proto.proxy2lobby.LobbyHealthReportAckOuterClass
import ink.pmc.common.exchange.proto.proxy2server.ExchangeCheckAckOuterClass
import ink.pmc.common.exchange.proto.proxy2server.ExchangeStartAckOuterClass
import ink.pmc.common.exchange.proto.server2lobby.ExchangeEndOuterClass
import ink.pmc.common.exchange.proto.server2lobby.ExchangeStartOuterClass
import ink.pmc.common.utils.proto.operation.ResultMessageOuterClass
import ink.pmc.common.utils.proto.player.PlayerOuterClass
import kotlinx.coroutines.flow.Flow

object ExchangeRpc : ExchangeRpcGrpcKt.ExchangeRpcCoroutineImplBase() {

    override suspend fun startExchange(request: ExchangeStartOuterClass.ExchangeStart): ExchangeStartAckOuterClass.ExchangeStartAck {
        return super.startExchange(request)
    }

    override suspend fun endExchange(request: ExchangeEndOuterClass.ExchangeEnd): ResultMessageOuterClass.ResultMessage {
        return super.endExchange(request)
    }

    override suspend fun isInExchange(request: PlayerOuterClass.Player): ExchangeCheckAckOuterClass.ExchangeCheckAck {
        return super.isInExchange(request)
    }

    override suspend fun notifyItemDistribute(request: ItemDistributeNotifyOuterClass.ItemDistributeNotify): ResultMessageOuterClass.ResultMessage {
        return super.notifyItemDistribute(request)
    }

    override suspend fun reportLobbyHealth(request: LobbyHealthReportOuterClass.LobbyHealthReport): LobbyHealthReportAckOuterClass.LobbyHealthReportAck {
        return super.reportLobbyHealth(request)
    }

    override fun monitorItemDistribute(request: Empty): Flow<ItemDistributeNotifyOuterClass.ItemDistributeNotify> {
        return super.monitorItemDistribute(request)
    }

}