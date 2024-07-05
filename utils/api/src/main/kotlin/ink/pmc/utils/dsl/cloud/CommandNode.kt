package ink.pmc.utils.dsl.cloud

import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.context.CommandContext

typealias ContextReceiver<C> = suspend CommandContext<C>.() -> Unit

data class CommandNode<C>(
    val prefix: CommandNodePrefix,
    val permission: String = "",
    val arguments: List<CommandComponent<C>>,
    val handler: ContextReceiver<C> = {}
) {

    val subNodes = mutableListOf<CommandNode<C>>()

    fun append(node: CommandNode<C>) {
        subNodes.add(node)
    }

}

val <C> CommandContext<C>.sender: C
    get() = sender()