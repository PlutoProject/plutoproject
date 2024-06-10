package ink.pmc.visual.renderers

import ink.pmc.utils.chat.nms
import ink.pmc.utils.player.sendPacket
import ink.pmc.utils.structure.emptyOptional
import ink.pmc.utils.structure.optional
import ink.pmc.visual.api.toast.Toast
import ink.pmc.visual.api.toast.ToastRenderer
import net.minecraft.advancements.*
import net.minecraft.advancements.critereon.ImpossibleTrigger
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket
import net.minecraft.resources.ResourceLocation
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Suppress("UNUSED")
object PaperToastRenderer : ToastRenderer<Player>() {

    private val location = optional(ResourceLocation("visual", "paper_toast_renderer"))
    private val criteria = mapOf("for_free" to Criterion(ImpossibleTrigger(), ImpossibleTrigger.TriggerInstance()))
    private val fixedRequirements = listOf("for_free")
    private val requirements = AdvancementRequirements(listOf(fixedRequirements))
    private val adventureTexture =
        optional(ResourceLocation("minecraft:textures/gui/advancements/backgrounds/adventure.png"))
    private val progress = mapOf(location.get() to AdvancementProgress().apply {
        update(requirements)
        getCriterion("for_free")?.grant()
    })

    override fun render(player: Player, obj: Toast) {
        val material = Material.getMaterial(obj.icon) ?: throw IllegalStateException("Invalid material: ${obj.icon}")
        val item = ItemStack(material, 1)
        val display = optional(
            DisplayInfo(
                CraftItemStack.asNMSCopy(item),
                obj.title.nms,
                obj.description.nms,
                adventureTexture,
                AdvancementType.GOAL,
                true,
                true,
                false,
            )
        )

        val advancement = Advancement(emptyOptional(), display, AdvancementRewards.EMPTY, criteria, requirements, false)
        val holder = AdvancementHolder(location.get(), advancement)
        val displayPacket = ClientboundUpdateAdvancementsPacket(false, listOf(holder), setOf(), progress)
        player.sendPacket(displayPacket)

        val removed = setOf(location.get())
        val removePacket = ClientboundUpdateAdvancementsPacket(false, listOf(), removed, mapOf<ResourceLocation, AdvancementProgress>())
        player.sendPacket(removePacket)
    }

}