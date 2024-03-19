package ink.pmc.common.utils.concurrent

import ink.pmc.common.utils.platform.paperUtilsPlugin
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
    this.scheduler.execute(paperUtilsPlugin, task, retired, delay)
}

@Suppress("UNUSED")
fun globalRegionScheduler(task: () -> Unit) {
    Bukkit.getServer().globalRegionScheduler.execute(paperUtilsPlugin, task)
}

@Suppress("UNUSED")
fun regionScheduler(location: Location, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(paperUtilsPlugin, location, task)
}

@Suppress("UNUSED")
fun regionScheduler(chunk: Chunk, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(paperUtilsPlugin, chunk.world, chunk.x, chunk.z, task)
}

fun Chunk.scheduler(task: () -> Unit) {
    regionScheduler(this, task)
}

fun Location.scheduler(task: () -> Unit) {
    regionScheduler(this, task)
}