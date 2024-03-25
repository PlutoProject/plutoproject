package ink.pmc.common.server.impl.velocity.player.remote

import ink.pmc.common.server.Server
import ink.pmc.common.server.player.PlayerOperator
import ink.pmc.common.server.player.ServerPlayer
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title

class VelocityRemotePlayerOperator : PlayerOperator, VelocityRemoteEntityOperator() {

    override fun sendMessage(player: ServerPlayer, component: Component) {
        TODO("Not yet implemented")
    }

    override fun sendActionbar(player: ServerPlayer, component: Component) {
        TODO("Not yet implemented")
    }

    override fun sendTitle(player: ServerPlayer, title: Title) {
        TODO("Not yet implemented")
    }

    override fun playSound(player: ServerPlayer, sound: Sound) {
        TODO("Not yet implemented")
    }

    override fun switchServer(player: ServerPlayer, target: Server) {
        TODO("Not yet implemented")
    }

    override fun getDisplayName(player: ServerPlayer): Component? {
        TODO("Not yet implemented")
    }

    override fun setDisplayName(player: ServerPlayer, component: Component) {
        TODO("Not yet implemented")
    }

}