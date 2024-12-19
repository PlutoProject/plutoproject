package ink.pmc.framework

import ink.pmc.framework.jvm.findClass
import java.io.File
import java.util.logging.Logger

lateinit var frameworkLogger: Logger
lateinit var frameworkDataFolder: File

inline val frameworkClassLoader
    get() = findClass("ink.pmc.framework.PaperPlugin")?.classLoader
        ?: findClass("ink.pmc.framework.VelocityPlugin")?.classLoader
        ?: error("Cannot find framework plugin class")

inline val runtimeClassLoader
    get() = findClass("ink.pmc.runtime.PaperPlugin")?.classLoader
        ?: findClass("ink.pmc.runtime.VelocityPlugin")?.classLoader
        ?: error("Cannot find runtime plugin class")