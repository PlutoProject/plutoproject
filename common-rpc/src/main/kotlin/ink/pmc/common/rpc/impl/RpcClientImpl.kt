package ink.pmc.common.rpc.impl

import ink.pmc.common.rpc.IRpcClient
import ink.pmc.common.rpc.serverLogger
import io.grpc.ManagedChannel
import io.grpc.StatusException
import io.grpc.okhttp.OkHttpChannelBuilder
import java.io.Closeable

class RpcClientImpl(private val host: String, private val port: Int) : IRpcClient, Closeable {

    private var started = false
    private var closed = false
    override lateinit var channel: ManagedChannel

    fun start() {
        if (closed) {
            throw IllegalStateException("RPC Client already closed")
        }

        while (!::channel.isInitialized) {
            try {
                channel = OkHttpChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .build()
                serverLogger.info("Connected to gRPC server.")
                started = true
            } catch (e: StatusException) {
                serverLogger.severe("Failed to connect gRPC server, waiting 5s before retry.")
                Thread.sleep(5000)
            }
        }
    }

    override fun close() {
        if (closed) {
            throw IllegalStateException("RPC Client already closed")
        }

        channel.shutdown()
        closed = true
    }

}