package ink.pmc.utils.command

import ink.pmc.utils.annotation.Command
import io.github.classgraph.ClassGraph
import org.incendo.cloud.CommandManager
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

fun <C> CommandManager<C>.registerCommands(
    packageName: String,
    metadata: (String) -> Pair<Boolean, Array<String>>
) {
    val scanResult = ClassGraph()
        .acceptPackages(packageName)
        .scan()

    scanResult.allClasses.forEach {
        val cls = Class.forName(it.name)
        cls.declaredMethods.forEach f@{ fn ->
            val function = fn.kotlinFunction ?: return@f
            val annotation = function.findAnnotation<Command>() ?: return@f
            val name = annotation.name
            val meta = metadata(name)

            if (!meta.first) {
                return@f
            }

            val aliases = meta.second
            function.call(this, aliases)
        }
    }
}