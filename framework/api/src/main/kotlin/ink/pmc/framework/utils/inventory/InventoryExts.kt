package ink.pmc.framework.utils.inventory

import ink.pmc.framework.utils.player.sendPacket
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftContainer
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView

fun InventoryView.title(component: Component) {
    val player = player as Player
    val craftPlayer = player as CraftPlayer
    val serverPlayer = craftPlayer.handle
    val id = serverPlayer.containerMenu.containerId
    val type = CraftContainer.getNotchInventoryType(this.topInventory)
    val packet = ClientboundOpenScreenPacket(id, type, PaperAdventure.asVanilla(component))
    player.sendPacket(packet)
    serverPlayer.containerMenu.sendAllDataToRemote()
}
