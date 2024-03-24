package ink.pmc.common.server.player

import ink.pmc.common.server.Server
import ink.pmc.common.server.entity.EntityOperator
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title

interface PlayerOperator<T> : EntityOperator<T> {

    fun sendMessage(serverPlayer: ServerPlayer, component: Component)

    fun sendActionbar(serverPlayer: ServerPlayer, component: Component)

    fun sendTitle(serverPlayer: ServerPlayer, title: Title)

    fun playSound(serverPlayer: ServerPlayer, sound: Sound)

    fun switchServer(serverPlayer: ServerPlayer, target: Server)

}