package ink.pmc.framework.utils.command.suggestion

import ink.pmc.framework.utils.dsl.cloud.sender
import org.bukkit.command.CommandSender
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.suggestion.SuggestionProvider

class PaperPrivilegedSuggestion<T : CommandSender>(
    wrap: SuggestionProvider<T>,
    permission: String
) : PrivilegedSuggestion<T>(wrap, permission) {

    companion object {
        fun <T : CommandSender> of(
            wrap: SuggestionProvider<T>,
            permission: String
        ): SuggestionProvider<T> {
            return PaperPrivilegedSuggestion(wrap, permission)
        }
    }

    override fun hasPermission(context: CommandContext<T>, permission: String): Boolean {
        return context.sender.hasPermission(permission)
    }

}