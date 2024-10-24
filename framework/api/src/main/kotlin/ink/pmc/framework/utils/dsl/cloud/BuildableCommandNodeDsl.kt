package ink.pmc.framework.utils.dsl.cloud

import ink.pmc.framework.utils.structure.Builder

open class BuildableCommandNodeDsl<C> : CommandNodeDsl<C>(), Builder<CommandNode<C>> {

    override fun build(): CommandNode<C> {
        return CommandNode(CommandNodePrefix(name, aliases.toTypedArray()), permission, arguments, flags, handler, builderReceiver)
            .apply { this@BuildableCommandNodeDsl.subNodes.forEach { append(it) } }
    }

}