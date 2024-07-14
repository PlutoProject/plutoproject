package ink.pmc.messages

import com.velocitypowered.api.proxy.server.RegisteredServer
import net.kyori.adventure.text.Component

data class Group(
    val name: String,
    val servers: List<RegisteredServer>,
    val joinMessage: Component?,
    val quitMessage: Component?
)