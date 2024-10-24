package ink.pmc.rpc.api

import ink.pmc.framework.utils.inject.inlinedGet
import io.grpc.Channel

interface RpcClient {
    companion object : RpcClient by inlinedGet()

    val channel: Channel

    fun start()

    fun stop()
}