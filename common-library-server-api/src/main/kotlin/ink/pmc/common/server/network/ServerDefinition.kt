package ink.pmc.common.server.network

import java.net.InetAddress

interface ServerDefinition {

    val name: String
    val address: InetAddress

}