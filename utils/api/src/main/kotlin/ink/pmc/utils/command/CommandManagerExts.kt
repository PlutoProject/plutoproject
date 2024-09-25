package ink.pmc.utils.command

import ink.pmc.utils.annotation.Command
import io.github.classgraph.ClassGraph
import org.incendo.cloud.CommandManager
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

data class CommandRegistrationResult(
    val enabled: Boolean = true,
    val aliases: Array<String> = arrayOf()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandRegistrationResult

        if (enabled != other.enabled) return false
        if (!aliases.contentEquals(other.aliases)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + aliases.contentHashCode()
        return result
    }

}

fun <C> CommandManager<C>.registerCommands(
    packageName: String,
    result: (String) -> CommandRegistrationResult = { _ -> CommandRegistrationResult() }
) {
    ClassGraph()
        .acceptPackages(packageName)
        .scan().use { scanResult ->
            scanResult.allClasses.forEach {
                val cls = Class.forName(it.name)
                cls.declaredMethods.forEach f@{ fn ->
                    val function = fn.kotlinFunction ?: return@f
                    val annotation = function.findAnnotation<Command>() ?: return@f
                    val name = annotation.name
                    val meta = result(name)

                    if (!meta.enabled) {
                        return@f
                    }

                    val aliases = meta.aliases
                    function.call(this, aliases)
                }
            }
        }
}