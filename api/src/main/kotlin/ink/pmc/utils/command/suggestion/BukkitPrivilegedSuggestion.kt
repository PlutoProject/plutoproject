package ink.pmc.utils.command.suggestion

import ink.pmc.utils.dsl.cloud.sender
import org.bukkit.command.CommandSender
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.suggestion.SuggestionProvider

class BukkitPrivilegedSuggestion<T : CommandSender>(
    wrap: SuggestionProvider<T>,
    permission: String
) : PrivilegedSuggestion<T>(wrap, permission) {

    companion object {
        fun <T : CommandSender> of(
            wrap: SuggestionProvider<T>,
            permission: String
        ): SuggestionProvider<T> {
            return BukkitPrivilegedSuggestion(wrap, permission)
        }
    }

    override fun hasPermission(context: CommandContext<T>, permission: String): Boolean {
        return context.sender.hasPermission(permission)
    }

}

fun <T : CommandSender> SuggestionProvider<T>.privileged(permission: String) =
    BukkitPrivilegedSuggestion.of(this, permission)