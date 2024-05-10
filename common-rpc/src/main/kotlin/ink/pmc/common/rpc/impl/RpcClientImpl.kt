package ink.pmc.common.rpc.impl

import ink.pmc.common.rpc.IRpcClient
import ink.pmc.common.rpc.serverLogger
import io.grpc.ManagedChannel
import io.grpc.StatusException
import io.grpc.kotlin.AbstractCoroutineStub
import io.grpc.okhttp.OkHttpChannelBuilder
import java.io.Closeable
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class RpcClientImpl(private val host: String, private val port: Int) : IRpcClient, Closeable {

    private var started = false
    private var closed = false
    private val cachedStubs = mutableMapOf<KClass<*>, AbstractCoroutineStub<*>>()
    private lateinit var channel: ManagedChannel

    override fun <T : AbstractCoroutineStub<T>> stub(cls: KClass<T>): T {
        if (closed) {
            throw IllegalStateException("RPC Client already closed")
        }

        if (cachedStubs.containsKey(cls)) {
            return cachedStubs[cls] as T
        }

        return cls.constructors.first().call(channel).also {
            cachedStubs[cls] = it
        }
    }

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