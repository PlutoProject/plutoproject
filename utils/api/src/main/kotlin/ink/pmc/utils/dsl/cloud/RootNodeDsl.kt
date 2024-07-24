package ink.pmc.utils.dsl.cloud

import org.incendo.cloud.Command
import org.incendo.cloud.CommandManager
import org.incendo.cloud.kotlin.MutableCommandBuilder
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.kotlin.extension.commandBuilder

class RootNodeDsl<C : Any> : BuildableCommandNodeDsl<C>() {

    fun build(commandManager: CommandManager<C>): List<Command<C>> {
        return createBuilder(commandManager, build()).map { it.build() }
    }

    private fun MutableCommandBuilder<C>.applyProperty(node: CommandNode<C>): MutableCommandBuilder<C> {
        return apply {
            if (node.permission != null) {
                permission(node.permission)
            }
            node.arguments.forEach { argument(it) }
            node.handler?.let { handler -> suspendingHandler { it.handler() } }
        }
    }

    private fun createBuilder(
        commandManager: CommandManager<C>,
        node: CommandNode<C>,
        parent: MutableCommandBuilder<C>? = null
    ): List<MutableCommandBuilder<C>> {
        if (parent == null) {
            return mutableListOf<MutableCommandBuilder<C>>().apply {
                val rootBuilder = commandManager.commandBuilder(node.prefix.name, aliases = node.prefix.aliases) {}
                node.handler?.let { add(rootBuilder.copy().applyProperty(node)) }
                node.subNodes.forEach {
                    addAll(createBuilder(commandManager, it, rootBuilder))
                }
            }
        }
        return mutableListOf<MutableCommandBuilder<C>>().apply {
            val childBuilder = parent.copy().literal(node.prefix.name, aliases = node.prefix.aliases)
            node.handler?.let { add(childBuilder.copy().applyProperty(node)) }
            node.subNodes.forEach {
                addAll(createBuilder(commandManager, it, childBuilder))
            }
        }
    }
}

inline operator fun <C : Any> CommandManager<C>.invoke(
    name: String,
    vararg aliases: String,
    block: RootNodeDsl<C>.() -> Unit
) {
    RootNodeDsl<C>()
        .apply { this.name = name }
        .apply { this.aliases.addAll(aliases) }
        .apply(block)
        .build(this)
        .forEach { command(it) }
}