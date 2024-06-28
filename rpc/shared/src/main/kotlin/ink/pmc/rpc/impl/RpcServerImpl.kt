package ink.pmc.rpc.impl

import ink.pmc.rpc.api.IRpcServer
import ink.pmc.rpc.serverLogger
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.Server
import io.grpc.ServerBuilder
import java.io.Closeable

class RpcServerImpl(private val port: Int) : IRpcServer, Closeable {

    private val serverBuilder = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
    private var started = false
    private var closed = false
    lateinit var server: Server
        private set

    override fun apply(block: ServerBuilder<*>.() -> Unit) {
        if (started) {
            throw IllegalStateException("RPC Server already started")
        }

        block.invoke(serverBuilder)
    }

    fun start() {
        if (closed) {
            throw IllegalStateException("RPC Server already closed")
        }

        try {
            server = serverBuilder.build().start()
            started = true
            serverLogger.info("Started gRPC server at $port.")
        } catch (e: Exception) {
            serverLogger.info("Failed to launch gRPC server!")
            e.printStackTrace()
        }
    }

    override fun close() {
        if (closed) {
            throw IllegalStateException("RPC Server already closed")
        }

        server.shutdown()
        closed = true
    }
}