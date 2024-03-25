package ink.pmc.common.server.impl

import io.netty.bootstrap.Bootstrap

@Suppress("UNUSED")
abstract class GameServerService : NettyServerService() {

    val bootstrap = Bootstrap()

}