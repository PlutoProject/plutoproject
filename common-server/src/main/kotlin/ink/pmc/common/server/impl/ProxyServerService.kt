package ink.pmc.common.server.impl

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup

@Suppress("UNUSED")
abstract class ProxyServerService : NettyServerService() {

    val bossGroup = NioEventLoopGroup()
    val bootstrap = ServerBootstrap()

    override fun close() {
        super.close()
        bossGroup.shutdownGracefully()
    }

}