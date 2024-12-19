package ink.pmc.framework.visual.display.text.renderers

import ink.pmc.framework.player.sendPacket
import ink.pmc.framework.chat.bukkitColor
import ink.pmc.framework.visual.display.text.TextDisplayRenderer
import ink.pmc.framework.visual.display.text.TextDisplayView
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.Vec3
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftTextDisplay
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import net.minecraft.world.entity.Display.TextDisplay as NmsTextDisplay

@Suppress("UNCHECKED_CAST")
open class NmsTextDisplayRenderer : TextDisplayRenderer {
    private val uuidToIdMap = ConcurrentHashMap<UUID, Int>()

    override fun render(viewer: Player, view: TextDisplayView) {
        remove(viewer, view)
        spawn(viewer, view)
    }

    override fun spawn(viewer: Player, view: TextDisplayView) {
        val id = randomId()
        val uuid = view.uuid
        val loc = view.location
        val world = (loc.world as CraftWorld).handle
        val server = Bukkit.getServer() as CraftServer
        val entity = NmsTextDisplay(EntityType.TEXT_DISPLAY, world)
        val craftEntity = CraftTextDisplay(server, entity)
        val options = view.options

        craftEntity.alignment = options.alignment
        craftEntity.backgroundColor = options.background.bukkitColor
        craftEntity.isDefaultBackground = options.isDefaultBackground
        craftEntity.lineWidth = options.lineWidth
        craftEntity.isSeeThrough = options.isSeeThrough
        craftEntity.isShadowed = options.shadow
        craftEntity.textOpacity = options.opacity.toByte()
        craftEntity.text(view.contents.fold(Component.empty()) { i, e ->
            i.append(e).let {
                if (view.contents.last() != e) i.appendNewline() else it
            } as TextComponent
        })

        val spawn = ClientboundAddEntityPacket(
            id,
            uuid,
            loc.x,
            loc.y,
            loc.z,
            loc.yaw,
            loc.pitch,
            EntityType.TEXT_DISPLAY,
            0,
            Vec3.ZERO,
            0.0
        )
        // DataValue 的 list 无论如何都 non-null
        val data = ClientboundSetEntityDataPacket(id, entity.entityData.packAll()!!)

        viewer.sendPacket(spawn)
        viewer.sendPacket(data)

        uuidToIdMap[uuid] = id
    }

    override fun remove(viewer: Player, view: TextDisplayView) {
        val id = uuidToIdMap[view.uuid] ?: return
        val packet = ClientboundRemoveEntitiesPacket(id)
        viewer.sendPacket(packet)
    }

    private fun randomId(): Int {
        return Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
    }
}