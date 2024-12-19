package ink.pmc.framework.rpc

import ink.pmc.framework.inject.inlinedGet
import io.grpc.Server
import io.grpc.ServerBuilder

interface RpcServer {
    companion object : RpcServer by inlinedGet()

    val server: Server

    fun apply(block: ServerBuilder<*>.() -> Unit)

    fun start()

    fun stop()
}