package ink.pmc.utils.inventory

import ink.pmc.utils.player.sendPacket
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftContainer
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView

fun InventoryView.updateTitle(component: Component) {
    val player = this as Player
    val serverPlayer = (this as CraftPlayer).handle
    val id = serverPlayer.containerMenu.containerId
    val type = CraftContainer.getNotchInventoryType(this.topInventory)
    val packet = ClientboundOpenScreenPacket(id, type, PaperAdventure.asVanilla(component))
    player.sendPacket(packet)
}
