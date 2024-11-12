package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.AbstractBridgeCommand
import ink.pmc.framework.bridge.player.BridgePlayer
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object BridgeCommand : AbstractBridgeCommand<CommandSender>() {
    override fun CommandSender.message(component: Component) {
        sendMessage(component)
    }

    @Command("backendbridge list_servers")
    @Permission("bridge.command")
    fun CommandSender.commandListServers() {
        listServers()
    }

    @Command("backendbridge list_players")
    @Permission("bridge.command")
    suspend fun CommandSender.commandListPlayers() {
        listPlayers()
    }

    @Command("backendbridge player <player> teleport <other>")
    @Permission("bridge.command")
    suspend fun CommandSender.commandTeleport(
        player: BridgePlayer,
        other: BridgePlayer
    ) {
        teleport(player, other)
    }

    @Command("backendbridge player <player> send_message <message>")
    @Permission("bridge.command")
    suspend fun CommandSender.commandSendMessage(
        player: BridgePlayer,
        @Argument("message", parserName = "bridge-component") message: Component
    ) {
        sendMessage(player, message)
    }

    @Command("backendbridge list_worlds")
    @Permission("bridge.command")
    fun CommandSender.commandListWorlds() {
        listWorlds()
    }
}