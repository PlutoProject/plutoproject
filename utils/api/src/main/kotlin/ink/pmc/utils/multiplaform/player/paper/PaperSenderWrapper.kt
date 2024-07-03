package ink.pmc.utils.multiplaform.player.paper

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.utils.multiplaform.SenderWrapper
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

open class PaperSenderWrapper<T : CommandSender>(override val original: T) : SenderWrapper<T> {

    override fun sendMessage(content: Component) {
        original.sendMessage(content)
    }

    override fun sendMessage(content: RootComponentKt.() -> Unit) {
        sendMessage(RootComponentKt().apply(content).build())
    }

}