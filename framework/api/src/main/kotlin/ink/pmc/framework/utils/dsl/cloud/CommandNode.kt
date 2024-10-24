package ink.pmc.framework.utils.dsl.cloud

import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.parser.flag.CommandFlag

typealias ContextReceiver<C> = suspend CommandContext<C>.() -> Unit

data class CommandNode<C>(
    val prefix: CommandNodePrefix,
    val permission: String? = null,
    val arguments: List<CommandComponent<C>>,
    val flags: List<CommandFlag<*>>,
    val handler: ContextReceiver<C>? = null,
    val builderReceiver: BuilderReceiver<C>? = null
) {

    val subNodes = mutableListOf<CommandNode<C>>()

    fun append(node: CommandNode<C>) {
        subNodes.add(node)
    }

}

val <C> CommandContext<C>.sender: C
    get() = sender()