package ink.pmc.common.server.player

import ink.pmc.common.server.Server
import ink.pmc.common.server.entity.EntityOperator
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title

interface PlayerOperator : EntityOperator {

    fun sendMessage(player: ServerPlayer, component: Component)

    fun sendActionbar(player: ServerPlayer, component: Component)

    fun sendTitle(player: ServerPlayer, title: Title)

    fun playSound(player: ServerPlayer, sound: Sound)

    fun switchServer(player: ServerPlayer, target: Server)

    fun getDisplayName(player: ServerPlayer): Component?

    fun setDisplayName(player: ServerPlayer, component: Component)

}