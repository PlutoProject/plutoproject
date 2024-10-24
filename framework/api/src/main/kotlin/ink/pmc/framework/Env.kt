package ink.pmc.framework

import ink.pmc.framework.utils.jvm.findClass
import java.util.logging.Logger

lateinit var frameworkLogger: Logger

inline val frameworkClassLoader
    get() = findClass("ink.pmc.framework.PaperPlugin")?.classLoader
        ?: findClass("ink.pmc.framework.VelocityPlugin")?.classLoader
        ?: error("Cannot find framework plugin class")