package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.BaseExchangeServiceImpl
import ink.pmc.common.exchange.proto.ExchangeRpcGrpcKt
import ink.pmc.common.rpc.RpcClient
import org.bukkit.entity.Player

abstract class AbstractBackendExchangeService : BaseExchangeServiceImpl<Player>() {

    val stub = ExchangeRpcGrpcKt.ExchangeRpcCoroutineStub(RpcClient.channel)

}