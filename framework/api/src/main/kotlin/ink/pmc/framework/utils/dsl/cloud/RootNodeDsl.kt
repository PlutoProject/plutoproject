package ink.pmc.framework.utils.dsl.cloud

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
            node.flags.forEach { commandBuilder.flag(it) }
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
                node.builderReceiver?.let { rootBuilder.commandBuilder.it() }
                node.handler?.let { add(rootBuilder.copy().applyProperty(node)) }
                node.subNodes.forEach {
                    addAll(createBuilder(commandManager, it, rootBuilder))
                }
            }
        }
        return mutableListOf<MutableCommandBuilder<C>>().apply {
            /*
            * 由于 Brigadier 原因，Cloud 暂时无法处理 literal 的 alias。
            * 此处为每个 alias 都单独创建一个 Node 来实现 alias 的效果。
            * 见：https://github.com/Incendo/cloud-minecraft/issues/5
            * */
            fun createChild(name: String): MutableCommandBuilder<C> {
                return parent.copy().literal(name).apply {
                    node.builderReceiver?.let { commandBuilder.it() }
                    node.handler?.let { add(copy().applyProperty(node)) }
                    node.subNodes.forEach {
                        addAll(createBuilder(commandManager, it, this))
                    }
                }
            }

            createChild(node.prefix.name)
            node.prefix.aliases.forEach { createChild(it) }
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