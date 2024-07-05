package ink.pmc.utils.dsl.cloud

import ink.pmc.utils.structure.Builder

open class BuildableCommandNodeDsl<C> : CommandNodeDsl<C>(), Builder<CommandNode<C>> {

    override fun build(): CommandNode<C> {
        return CommandNode(CommandNodePrefix(name, aliases.toTypedArray()), permission, arguments, handler)
            .apply { this@BuildableCommandNodeDsl.subNodes.forEach { append(it) } }
    }

}