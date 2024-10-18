package ink.pmc.utils.jvm

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

private val cachedClasses = mutableMapOf<String, Class<*>>()
private val cachedConstructors = mutableMapOf<String, Constructor<*>>()
private val cachedMethods = mutableMapOf<String, Method>()
private val cachedFields = mutableMapOf<String, Field>()

@Suppress("UNUSED")
fun reflect(name: String): Class<*> {
    return cachedClasses.computeIfAbsent(name) { Class.forName(name) }
}

fun Class<*>.method(name: String, vararg types: Class<*>): Method {
    return cachedMethods.computeIfAbsent(canonicalName) {
        getMethod(name, *types).also {
            it.isAccessible = true
        }
    }
}

fun Class<*>.constructor(vararg types: Class<*>): Constructor<*> {
    return cachedConstructors.computeIfAbsent(canonicalName) {
        getConstructor(*types).also {
            it.isAccessible = true
        }
    }
}

fun Class<*>.field(name: String): Field {
    return cachedFields.computeIfAbsent(canonicalName) {
        getField(name).also {
            it.isAccessible = true
        }
    }
}