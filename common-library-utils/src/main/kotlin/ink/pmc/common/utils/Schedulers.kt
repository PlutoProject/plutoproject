package ink.pmc.common.utils

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.java.JavaPlugin

fun Entity.execute(plugin: JavaPlugin, retired: () -> Unit = {}, delay: Long = 0L, task: () -> Unit) {
    this.scheduler.execute(plugin, task, retired, delay)
}

fun globalRegionScheduler(plugin: JavaPlugin, task: () -> Unit) {
    Bukkit.getServer().globalRegionScheduler.execute(plugin, task)
}

fun regionScheduler(plugin: JavaPlugin, location: Location, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(plugin, location, task)
}

fun regionScheduler(plugin: JavaPlugin, chunk: Chunk, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(plugin, chunk.world, chunk.x, chunk.z, task)
}