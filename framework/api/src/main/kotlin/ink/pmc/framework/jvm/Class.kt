package ink.pmc.framework.jvm

import io.github.classgraph.ClassGraph
import net.bytebuddy.ByteBuddy
import net.bytebuddy.agent.ByteBuddyAgent

@Suppress("UNUSED")
val byteBuddy = ByteBuddy().also {
    ByteBuddyAgent.install()
}

@Suppress("UNUSED")
fun loadClassesInPackages(
    vararg packageName: String,
    classLoader: ClassLoader = Thread.currentThread().contextClassLoader
) {
    ClassGraph()
        .acceptPackages(*packageName)
        .scan().use { result ->
            result.allClasses.forEach {
                runCatching {
                    classLoader.loadClass(it.name)
                }
            }
        }
}

@Suppress("NOTHING_TO_INLINE")
inline fun findClass(fqn: String): Class<*>? {
    return runCatching {
        Class.forName(fqn)
    }.getOrNull()
}