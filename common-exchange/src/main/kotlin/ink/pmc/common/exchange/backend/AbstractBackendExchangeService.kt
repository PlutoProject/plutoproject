package ink.pmc.common.exchange.backend

import ink.pmc.common.exchange.proto.ExchangeRpcGrpcKt
import ink.pmc.common.exchange.service.BaseExchangeServiceImpl
import ink.pmc.common.rpc.RpcClient
import org.bukkit.entity.Player

abstract class AbstractBackendExchangeService : BaseExchangeServiceImpl<Player>() {

    val stub = ExchangeRpcGrpcKt.ExchangeRpcCoroutineStub(RpcClient.channel)

    abstract fun startBackGroundJobs()

    abstract suspend fun stopBackGroundJobs()

}