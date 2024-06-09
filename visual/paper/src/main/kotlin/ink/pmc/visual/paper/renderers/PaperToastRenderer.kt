package ink.pmc.visual.paper.renderers

import ink.pmc.utils.jvm.constructor
import ink.pmc.utils.jvm.field
import ink.pmc.utils.jvm.method
import ink.pmc.utils.jvm.reflect
import ink.pmc.visual.toast.Toast
import ink.pmc.visual.toast.ToastRenderer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object PaperToastRenderer : ToastRenderer<Player>() {

    private const val NAMESPACE = "common_visual"
    private const val KEY = "paper_toast_renderer"
    private val namespacedKey =
        NamespacedKey.fromString("$NAMESPACE:$KEY") ?: throw IllegalStateException("Failed to get advancement!")
    private val advancement = Bukkit.getAdvancement(namespacedKey)
    private val packet = reflect("net.minecraft.network.protocol.game.PacketPlayOutAdvancements")
    private val advancementDisplay = reflect("net.minecraft.advancements.AdvancementDisplay")
    private val advancementRewards = reflect("net.minecraft.advancements.AdvancementRewards")
    private val advancementRewardsInstance =
        advancementRewards.constructor(Int::class.java, List::class.java, List::class.java, Optional::class.java)
            .newInstance(
                0, listOf<Any>(), listOf<Any>(), Optional.ofNullable(null)
            )
    private val criterion = reflect("net.minecraft.advancements.Criterion")
    private val itemStack = reflect("net.minecraft.world.item.ItemStack")
    private val component = reflect("net.minecraft.network.chat.IChatBaseComponent")
    private val advancementType = reflect("net.minecraft.advancements.AdvancementFrameType")
    private val minecraftKey = reflect("net.minecraft.resources.MinecraftKey")
    private val minecraftKeyInstance =
        minecraftKey.constructor(String::class.java, String::class.java).newInstance(NAMESPACE, KEY)
    val advancementProgress = reflect("net.minecraft.advancements.AdvancementProgress")
    private val chatSerializer = reflect("net.minecraft.network.chat.IChatBaseComponent\$ChatSerializer")
    private val chatSerializerFromJson = chatSerializer.method("a", String::class.java)
    private val goal = advancementDisplay.field("GOAL")

    private fun advComponentToNms(component: Component): Any {
        val json = GsonComponentSerializer.gson().serialize(component)
        return chatSerializerFromJson.invoke(null, json)
    }

    private fun advancementDisplay(icon: String, title: Component, desc: Component): Any {
        val mat = Material.getMaterial(icon) ?: throw IllegalStateException("Can't find material: $icon")
        val itemStackInstance = ItemStack(mat, 1)
        val constructor = advancementDisplay.constructor(
            itemStack,
            component,
            component,
            Optional::class.java,
            advancementType,
            Boolean::class.java,
            Boolean::class.java,
            Boolean::class.java
        )

        return constructor.newInstance(
            itemStackInstance,
            advComponentToNms(title),
            advComponentToNms(desc),
            Optional.ofNullable(null),
            goal,
            true,
            false,
            false
        )
    }

    override fun render(player: Player, obj: Toast) {
        val advCriteria = mutableMapOf<String, Any>()
        val advReqs = arrayOf<String>()
    }

}