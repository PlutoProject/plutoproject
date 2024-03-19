package ink.pmc.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UNUSED")
val utilsPlugin = Bukkit.getServer().pluginManager.getPlugin("common-utils")!! as JavaPlugin

val coroutineScope = CoroutineScope(Dispatchers.Default)