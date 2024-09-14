package ink.pmc.framework

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

inline val frameworkPaper: Plugin
    get() = Bukkit.getServer().pluginManager.getPlugin("plutoproject_framework")!!