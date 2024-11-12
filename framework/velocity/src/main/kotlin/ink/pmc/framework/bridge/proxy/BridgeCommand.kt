package ink.pmc.framework.bridge.proxy

import com.velocitypowered.api.command.CommandSource
import ink.pmc.framework.bridge.AbstractBridgeCommand
import ink.pmc.framework.bridge.player.BridgePlayer
import net.kyori.adventure.text.Component
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object BridgeCommand : AbstractBridgeCommand<CommandSource>() {
    override fun CommandSource.message(component: Component) {
        sendMessage(component)
    }

    @Command("masterbridge list_servers")
    @Permission("bridge.command")
    fun CommandSource.commandListServers() {
        listServers()
    }

    @Command("masterbridge list_players")
    @Permission("bridge.command")
    suspend fun CommandSource.commandListPlayers() {
        listPlayers()
    }

    @Command("masterbridge player <player> teleport <other>")
    @Permission("bridge.command")
    suspend fun CommandSource.commandTeleport(
        player: BridgePlayer,
        other: BridgePlayer
    ) {
        teleport(player, other)
    }

    @Command("masterbridge player <player> send_message <message>")
    @Permission("bridge.command")
    suspend fun CommandSource.commandSendMessage(
        player: BridgePlayer,
        @Argument("message", parserName = "bridge-component") message: Component
    ) {
        sendMessage(player, message)
    }

    @Command("masterbridge list_worlds")
    @Permission("bridge.command")
    fun CommandSource.commandListWorlds() {
        listWorlds()
    }
}