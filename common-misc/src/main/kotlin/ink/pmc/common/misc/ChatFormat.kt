package ink.pmc.common.misc

import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.platform.paper
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

fun handleChatFormat(event: AsyncChatEvent) {
    val source = event.player
    val message = event.message()
    val component = CHAT_FORMAT
        .replace("<player>", Component.text(source.name))
        .replace("<message>", message)
    paper.broadcast(component)
    event.isCancelled = true
}

@Suppress("UNUSED", "DEPRECATION")
object MiscChatRender : ChatRenderer {

    override fun render(source: Player, sourceDisplayName: Component, message: Component, viewer: Audience): Component {
        /*
        * displayName 的 component 格式很怪异，会导致无法替换颜色。
        * */
        return CHAT_FORMAT
            .replace("<player>", Component.text(source.displayName))
            .replace("<message>", message)
    }

}