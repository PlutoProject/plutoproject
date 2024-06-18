package ink.pmc.utils.player

import net.minecraft.network.protocol.Packet
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.sendPacket(packet: Packet<*>) {
    this as CraftPlayer
    handle.connection.send(packet)
}