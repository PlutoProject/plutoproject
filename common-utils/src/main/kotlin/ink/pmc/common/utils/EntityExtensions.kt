package ink.pmc.common.utils

import org.bukkit.entity.Entity
import org.bukkit.plugin.java.JavaPlugin

fun Entity.execute(plugin: JavaPlugin, retired: () -> Unit = {}, delay: Long = 0L, task: () -> Unit) {
    this.scheduler.execute(plugin, task, retired, delay)
}