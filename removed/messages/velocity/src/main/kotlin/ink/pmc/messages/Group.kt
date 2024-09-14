package ink.pmc.messages

import net.kyori.adventure.text.Component

data class Group(
    val name: String,
    val servers: List<String>,
    val joinMessage: Component?,
    val quitMessage: Component?
)