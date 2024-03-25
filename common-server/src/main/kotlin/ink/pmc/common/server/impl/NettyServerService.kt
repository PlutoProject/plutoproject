package ink.pmc.common.server.impl

import ink.pmc.common.server.ServerService
import io.netty.channel.nio.NioEventLoopGroup
import java.io.Closeable

@Suppress("UNUSED")
abstract class NettyServerService : ServerService, Closeable {

    val workerGroup = NioEventLoopGroup()

    override fun close() {
        workerGroup.shutdownGracefully()
    }

}