package ink.pmc.utils.player

import net.minecraft.network.protocol.Packet
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.sendPacket(packet: Packet<*>) {
    this as CraftPlayer
    handle.connection.send(packet)
}