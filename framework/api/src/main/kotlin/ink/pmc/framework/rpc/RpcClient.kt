package ink.pmc.framework.rpc

import ink.pmc.framework.inject.inlinedGet
import io.grpc.Channel

interface RpcClient {
    companion object : RpcClient by inlinedGet()

    val channel: Channel

    fun start()

    fun stop()
}