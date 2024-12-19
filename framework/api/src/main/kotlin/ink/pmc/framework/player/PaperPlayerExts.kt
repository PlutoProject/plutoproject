package ink.pmc.framework.player

import com.google.common.io.ByteStreams
import ink.pmc.framework.frameworkPaper
import ink.pmc.framework.concurrent.io
import ink.pmc.framework.concurrent.submitSync
import ink.pmc.framework.platform.isAsync
import ink.pmc.framework.platform.isFolia
import net.minecraft.network.protocol.Packet
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.sendPacket(packet: Packet<*>) {
    this as CraftPlayer
    handle.connection.send(packet)
}

suspend fun Player.switchServer(name: String) {
    io {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(name)
        player?.sendPluginMessage(frameworkPaper, "BungeeCord", out.toByteArray())
    }
}

@Suppress("UNUSED")
fun Player.threadSafeTeleport(location: Location) {
    when {
        isFolia -> teleportAsync(location)
        isAsync -> submitSync { teleport(location) }
        else -> teleport(location)
    }
}