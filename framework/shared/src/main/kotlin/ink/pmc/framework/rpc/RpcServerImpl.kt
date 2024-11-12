package ink.pmc.framework.rpc

import ink.pmc.framework.FrameworkConfig
import ink.pmc.framework.frameworkLogger
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.Server
import io.grpc.ServerBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.logging.Level

class RpcServerImpl : RpcServer, KoinComponent {
    private val config by lazy { get<FrameworkConfig>().rpc }
    private val serverBuilder = Grpc.newServerBuilderForPort(config.port, InsecureServerCredentials.create())
    private var isRunning = false
    private var _server: Server? = null
    override val server: Server
        get() = checkNotNull(_server) { "Server not running" }

    override fun apply(block: ServerBuilder<*>.() -> Unit) {
        check(!isRunning) { "RPC server already running" }
        block.invoke(serverBuilder)
    }

    override fun start() {
        check(!isRunning) { "RPC server already running" }
        try {
            _server = serverBuilder
                .intercept(InternalErrorInterceptor)
                .build()
                .start()
            isRunning = true
            frameworkLogger.info("Running gRPC server at ${config.port}")
        } catch (e: Exception) {
            frameworkLogger.log(Level.SEVERE, "Failed to launch gRPC server", e)
        }
    }

    override fun stop() {
        check(isRunning) { "RPC server is not running" }
        _server?.shutdown()
        _server = null
        isRunning = false
    }
}