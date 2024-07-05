package ink.pmc.utils.dsl.cloud

import org.incendo.cloud.component.CommandComponent

open class CommandNodeDsl<C> {

    lateinit var name: String
    val aliases = mutableListOf<String>()
    var permission = ""
    var arguments = mutableListOf<CommandComponent<C>>()
    var handler: ContextReceiver<C> = {}
    protected val subNodes = mutableListOf<CommandNode<C>>()

    fun permission(node: String) {
        permission = node
    }

    fun argument(component: CommandComponent<C>) {
        arguments.add(component)
    }

    fun argument(component: CommandComponent.Builder<C, *>) {
        arguments.add(component.build())
    }

    fun handler(block: ContextReceiver<C>) {
        handler = block
    }

    fun node(node: CommandNode<C>) {
        subNodes.add(node)
    }

    operator fun String.invoke(block: CommandNodeDsl<C>.() -> Unit) {
        subNodes.add(
            BuildableCommandNodeDsl<C>()
                .apply { this.name = this@invoke }
                .apply(block)
                .build()
        )
    }

    operator fun CommandNodePrefix.invoke(block: CommandNodeDsl<C>.() -> Unit) {
        subNodes.add(
            BuildableCommandNodeDsl<C>()
                .apply { this.name = this@invoke.name }
                .apply { this.aliases.addAll(this@invoke.aliases) }
                .apply(block)
                .build()
        )
    }

}