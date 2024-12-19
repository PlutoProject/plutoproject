package ink.pmc.framework.command

import org.incendo.cloud.CommandManager
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.kotlin.coroutines.annotations.installCoroutineSupport

inline fun <reified C> CommandManager<C>.annotationParser(): AnnotationParser<C> {
    return AnnotationParser(this, C::class.java).installCoroutineSupport()
}