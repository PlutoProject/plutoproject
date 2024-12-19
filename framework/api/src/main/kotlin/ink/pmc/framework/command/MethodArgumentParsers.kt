package ink.pmc.framework.command

import ink.pmc.framework.jvm.findClass
import org.incendo.cloud.CommandManager
import org.incendo.cloud.annotations.parser.MethodArgumentParser
import org.incendo.cloud.parser.ArgumentParser

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE",  "UnusedReceiverParameter")
inline fun <C, T> CommandManager<C>.getKotlinMethodArgumentParser(): Class<ArgumentParser<C, T>> {
    return (findClass("org.incendo.cloud.kotlin.coroutines.annotations.KotlinMethodArgumentParser")
        ?: error("Env error: cannot find KotlinMethodArgumentParser class")) as Class<ArgumentParser<C, T>>
}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE",  "UnusedReceiverParameter")
inline fun <C, T> CommandManager<C>.getMethodArgumentParser(): Class<ArgumentParser<C, T>> {
    return MethodArgumentParser::class.java as Class<ArgumentParser<C, T>>
}