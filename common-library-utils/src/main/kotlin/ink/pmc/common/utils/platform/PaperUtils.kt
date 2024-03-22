package ink.pmc.common.utils.platform

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.Executor

@Suppress("UNUSED")
val paperUtilsPlugin = Bukkit.getServer().pluginManager.getPlugin("common-utils")!! as JavaPlugin

@Suppress("UNUSED")
val tpsLast1Minute: Double
    get() = Bukkit.getServer().tps[0]

@Suppress("UNUSED")
val tpsLast5Minute: Double
    get() = Bukkit.getServer().tps[1]

@Suppress("UNUSED")
val tpsLast15Minute: Double
    get() = Bukkit.getServer().tps[2]

@Suppress("UNUSED")
val currentMSPT: Double
    get() = Bukkit.getServer().averageTickTime

@Suppress("UNUSED")
lateinit var serverExecutor: Executor