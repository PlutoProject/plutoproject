package ink.pmc.rpc.api

import io.grpc.ServerBuilder

object RpcServer : IRpcServer by IRpcServer.instance

interface IRpcServer {

    companion object {
        lateinit var instance: IRpcServer
    }

    fun apply(block: ServerBuilder<*>.() -> Unit)

}