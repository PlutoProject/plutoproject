package ink.pmc.framework.utils.command.suggestion

import ink.pmc.framework.utils.dsl.cloud.sender
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.suggestion.SuggestionProvider

class PaperPrivilegedSuggestion<T : CommandSourceStack>(
    wrap: SuggestionProvider<T>,
    permission: String
) : PrivilegedSuggestion<T>(wrap, permission) {

    companion object {
        fun <T : CommandSourceStack> of(
            wrap: SuggestionProvider<T>,
            permission: String
        ): SuggestionProvider<T> {
            return PaperPrivilegedSuggestion(wrap, permission)
        }
    }

    override fun hasPermission(context: CommandContext<T>, permission: String): Boolean {
        return context.sender.sender.hasPermission(permission)
    }

}

fun <T : CommandSourceStack> SuggestionProvider<T>.privileged(permission: String) =
    PaperPrivilegedSuggestion.of(this, permission)