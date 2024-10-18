package ink.pmc.utils.player

import com.google.common.io.ByteStreams
import ink.pmc.utils.concurrent.io
import ink.pmc.utils.platform.paperUtilsPlugin
import net.minecraft.network.protocol.Packet
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
        player?.sendPluginMessage(paperUtilsPlugin, "BungeeCord", out.toByteArray())
    }
}