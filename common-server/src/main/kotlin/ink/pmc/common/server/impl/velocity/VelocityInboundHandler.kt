package ink.pmc.common.server.impl.velocity

import ink.pmc.common.server.impl.ProxyServerService
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class VelocityInboundHandler(private val serverService: ProxyServerService) : SimpleChannelInboundHandler<String>() {

    override fun channelRead0(p0: ChannelHandlerContext?, p1: String?) {

    }

}