package ink.pmc.common.server.impl.velocity.player.local

import ink.pmc.common.server.Server
import ink.pmc.common.server.ServerService
import ink.pmc.common.server.network.Proxy
import ink.pmc.common.server.player.PlayerOperator
import ink.pmc.common.server.player.ServerPlayer
import ink.pmc.common.server.velocity.player
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title

@Suppress("UNUSED")
class VelocityLocalPlayerOperator : PlayerOperator, VelocityLocalEntityOperator() {

    override fun sendMessage(player: ServerPlayer, component: Component) {
        player.player.sendMessage(component)
    }

    override fun sendActionbar(player: ServerPlayer, component: Component) {
        player.player.sendActionBar(component)
    }

    override fun sendTitle(player: ServerPlayer, title: Title) {
        player.player.showTitle(title)
    }

    override fun playSound(player: ServerPlayer, sound: Sound) {
        player.player.playSound(sound)
    }

    override fun switchServer(player: ServerPlayer, target: Server) {
        val proxy = ServerService.instance.server as Proxy
        proxy.transferServer(player, target)
    }

    override fun getDisplayName(player: ServerPlayer): Component? {
        throw UnsupportedOperationException(unsupported)
    }

    override fun setDisplayName(player: ServerPlayer, component: Component) {
        throw UnsupportedOperationException(unsupported)
    }

}