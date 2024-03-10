package ink.pmc.common.misc

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.entity.Player

fun handleChatFormat(event: AsyncChatEvent) {
    event.renderer(MiscChatRender)
}

object MiscChatRender : ChatRenderer {

    override fun render(source: Player, sourceDisplayName: Component, message: Component, viewer: Audience): Component {
        val nameReplacement = TextReplacementConfig.builder()
            .match("<player>")
            .replacement(sourceDisplayName)
            .build()

        val messageReplacement = TextReplacementConfig.builder()
            .match("<message>")
            .replacement(message)
            .build()

        return CHAT_FORMAT
            .replaceText(nameReplacement)
            .replaceText(messageReplacement)
    }

}