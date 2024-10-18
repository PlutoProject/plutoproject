package ink.pmc.utils.multiplaform.player.velocity

import com.velocitypowered.api.command.CommandSource
import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.utils.multiplaform.SenderWrapper
import net.kyori.adventure.text.Component

open class VelocitySenderWrapper<T : CommandSource>(override val original: T) : SenderWrapper<T> {

    override fun sendMessage(content: Component) {
        original.sendMessage(content)
    }

    override fun sendMessage(content: RootComponentKt.() -> Unit) {
        sendMessage(RootComponentKt().apply(content).build())
    }

}