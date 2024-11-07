package ink.pmc.framework.bridge.player

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.sound.SoundKt
import ink.pmc.advkt.title.TitleKt
import ink.pmc.framework.bridge.server.ServerElement
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title

interface BridgeSender : ServerElement {
    fun sendMessage(message: String)

    fun sendMessage(message: Component)

    fun sendMessage(component: RootComponentKt.() -> Unit)

    fun showTitle(title: Title)

    fun showTitle(title: TitleKt.() -> Unit)

    fun playSound(sound: Sound)

    fun playSound(sound: SoundKt.() -> Unit)

    fun performCommand(command: String)
}