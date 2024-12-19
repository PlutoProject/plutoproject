package ink.pmc.framework.command.suggestion

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
        return context.sender().hasPermission(permission)
    }
}

fun <T : CommandSender> SuggestionProvider<T>.privileged(permission: String) =
    PaperPrivilegedSuggestion.of(this, permission)