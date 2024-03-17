package ink.pmc.common.utils.concurrent

import ink.pmc.common.utils.utilsPlugin
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UNUSED")
fun Entity.scheduler(plugin: JavaPlugin, retired: () -> Unit = {}, delay: Long = 0L, task: () -> Unit) {
    this.scheduler.execute(plugin, task, retired, delay)
}

@Suppress("UNUSED")
fun globalRegionScheduler(plugin: JavaPlugin, task: () -> Unit) {
    Bukkit.getServer().globalRegionScheduler.execute(plugin, task)
}

@Suppress("UNUSED")
fun regionScheduler(plugin: JavaPlugin, location: Location, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(plugin, location, task)
}

@Suppress("UNUSED")
fun regionScheduler(plugin: JavaPlugin, chunk: Chunk, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(plugin, chunk.world, chunk.x, chunk.z, task)
}

@Suppress("UNUSED")
fun Entity.scheduler(retired: () -> Unit = {}, delay: Long = 0L, task: () -> Unit) {
    this.scheduler.execute(utilsPlugin, task, retired, delay)
}

@Suppress("UNUSED")
fun globalRegionScheduler(task: () -> Unit) {
    Bukkit.getServer().globalRegionScheduler.execute(utilsPlugin, task)
}

@Suppress("UNUSED")
fun regionScheduler(location: Location, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(utilsPlugin, location, task)
}

@Suppress("UNUSED")
fun regionScheduler(chunk: Chunk, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(utilsPlugin, chunk.world, chunk.x, chunk.z, task)
}