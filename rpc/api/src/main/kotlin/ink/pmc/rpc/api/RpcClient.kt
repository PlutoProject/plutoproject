package ink.pmc.rpc.api

import io.grpc.Channel

object RpcClient : IRpcClient by IRpcClient.instance

interface IRpcClient {

    companion object {
        lateinit var instance: IRpcClient
    }

    val channel: Channel
}