package ink.pmc.framework.rpc

import ink.pmc.framework.FrameworkConfig
import ink.pmc.framework.frameworkLogger
import io.grpc.Channel
import io.grpc.ManagedChannel
import io.grpc.StatusException
import io.grpc.okhttp.OkHttpChannelBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class RpcClientImpl : RpcClient, KoinComponent {
    private val config by lazy { get<FrameworkConfig>().rpc }
    private var isRunning = false
    private var _channel: ManagedChannel? = null
    override val channel: Channel
        get() = checkNotNull(_channel) { "RPC client is not connected yet" }

    override fun start() {
        check(!isRunning) { "RPC client already running" }
        while (_channel == null && !isRunning) {
            try {
                _channel = OkHttpChannelBuilder.forAddress(config.host, config.port)
                    .usePlaintext()
                    .build()
                isRunning = true
                frameworkLogger.info("Connected to gRPC server")
            } catch (e: StatusException) {
                frameworkLogger.severe("Failed to connect gRPC server, wait 5s before retry")
                Thread.sleep(5000)
            }
        }
    }

    override fun stop() {
        check(isRunning) { "RPC client is not running" }
        _channel?.shutdown()
        _channel = null
        isRunning = false
    }
}