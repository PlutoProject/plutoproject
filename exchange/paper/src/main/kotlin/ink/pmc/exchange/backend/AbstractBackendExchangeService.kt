package ink.pmc.exchange.backend

import ink.pmc.exchange.proto.ExchangeRpcGrpcKt
import ink.pmc.exchange.service.BaseExchangeServiceImpl
import ink.pmc.rpc.api.RpcClient
import org.bukkit.entity.Player

abstract class AbstractBackendExchangeService : BaseExchangeServiceImpl<Player>() {

    val stub = ExchangeRpcGrpcKt.ExchangeRpcCoroutineStub(RpcClient.channel)

    abstract fun startBackGroundJobs()

    abstract suspend fun stopBackGroundJobs()
}