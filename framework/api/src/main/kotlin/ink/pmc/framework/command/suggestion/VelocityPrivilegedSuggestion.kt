package ink.pmc.framework.command.suggestion

import com.velocitypowered.api.command.CommandSource
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.suggestion.SuggestionProvider

class VelocityPrivilegedSuggestion<T : CommandSource>(
    wrap: SuggestionProvider<T>,
    permission: String
) : PrivilegedSuggestion<T>(wrap, permission) {

    companion object {
        fun <T : CommandSource> of(
            wrap: SuggestionProvider<T>,
            permission: String
        ): SuggestionProvider<T> {
            return VelocityPrivilegedSuggestion(wrap, permission)
        }
    }

    override fun hasPermission(context: CommandContext<T>, perm: String): Boolean {
        return context.sender().hasPermission(perm)
    }

}

fun <T : CommandSource> SuggestionProvider<T>.privileged(permission: String) =
    VelocityPrivilegedSuggestion.of(this, permission)