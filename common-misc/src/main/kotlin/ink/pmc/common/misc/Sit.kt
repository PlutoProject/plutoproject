package ink.pmc.common.misc

import ink.pmc.common.misc.api.*
import ink.pmc.common.utils.regionScheduler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDismountEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

/*
* 有时候在尝试坐下的时候会由于未知原因直接触发 EntityDismountEvent，导致无法坐下。
* 猜测可能是和碰撞有关。设置一个延迟机制，来解决这个问题。
* */
val sitDelay = CopyOnWriteArraySet<UUID>()
val armorStandDataKey = NamespacedKey(plugin, "sit-passenger")

fun createArmorStand(locationToSit: Location): Entity {
    /*
    * TODO: 替换为能在 Folia 上使用的 API
    * 由于 Folia 目前似乎暂无完整的世界操作 API，所以先搁置
    * */
    val armorStandLoc = offsetLocation(locationToSit)

    val world = armorStandLoc.world
    val entity = world.spawn(armorStandLoc, ArmorStand::class.java)

    entity.setGravity(false)
    entity.isInvisible = true
    entity.setAI(false)

    return entity
}

@OptIn(DelicateCoroutinesApi::class)
fun markDelay(player: Player) {
    sitDelay.add(player.uniqueId)
    GlobalScope.launch {
        delay(100)
        sitDelay.remove(player.uniqueId)
    }
}

fun tryToStand(event: EntityEvent, needCancel: Boolean = true) {
    val entity = event.entity

    if (entity !is Player) {
        return
    }

    if (!entity.isSitting) {
        return
    }

    if (event is Cancellable) {
        event.isCancelled = needCancel
    }

    entity.stand()
}

fun handleSitClick(event: PlayerInteractEvent) {
    val player = event.player

    if (event.hand != EquipmentSlot.HAND) {
        return
    }

    if (!event.action.isRightClick) {
        return
    }

    if (player.inventory.itemInMainHand.type != Material.AIR) {
        return
    }

    if (event.clickedBlock == null) {
        return
    }

    val block = event.clickedBlock!!

    if (player.sitLocation == block.location) {
        return
    }

    val blockData = block.blockData

    if (blockData !is Slab && blockData !is Stairs) {
        return
    }

    val location = block.location

    if (!checkLocation(location)) {
        return
    }

    event.isCancelled = true

    player.sit(location)
}

fun markAsSeat(entity: Entity, sitter: Player) {
    entity.persistentDataContainer.set(armorStandDataKey, PersistentDataType.STRING, sitter.uniqueId.toString())
}

fun isSeat(entity: Entity): Boolean {
    return entity.persistentDataContainer.has(armorStandDataKey)
}

fun getSitter(entity: Entity): Player? {
    val uuid = UUID.fromString(entity.persistentDataContainer.get(armorStandDataKey, PersistentDataType.STRING))
    return plugin.server.getPlayer(uuid)
}

fun handlePlayerQuitStand(event: PlayerQuitEvent) {
    val player = event.player

    if (player.isSitting) {
        player.stand()
    }
}

fun handleSitDelay(event: EntityDismountEvent) {
    if (!sitDelay.contains(event.entity.uniqueId)) {
        tryToStand(event)
    } else {
        event.isCancelled = true
    }
}

fun handleSitLocationBroke(event: BlockBreakEvent) {
    val location = event.block.location.toBlockLocation()

    if (!sitManager.sitters.containsValue(location)) {
        return
    }

    event.player.stand()
}

fun handleArmorStandAction(event: Cancellable, needCancel: Boolean = true) {
    if (event !is EntityEvent) {
        return
    }

    val entity = event.entity

    if (entity !is ArmorStand) {
        return
    }

    if (!isSeat(entity) || getSitter(entity) == null) {
        return
    }

    event.isCancelled = needCancel
}

fun handleArmorStandClear(event: ChunkLoadEvent) {
    val chunk = event.chunk

    regionScheduler(plugin, chunk) {
        val entities = chunk.entities
        val armorStands = entities.filter { it.persistentDataContainer.has(armorStandDataKey) }

        armorStands.forEach {
            if (getSitter(it) != null && getSitter(it)?.seat != it) {
                it.remove()
                val location = it.location
                plugin.logger.info("Removed useless armor stand at ${location.x}, ${location.y}, ${location.z}, ${location.world.name}.")
            }
        }
    }
}

private fun isSlab(blockData: BlockData): Boolean {
    return blockData is Slab
}

private fun isStair(blockData: BlockData): Boolean {
    return blockData is Stairs
}

private fun treatAsNormal(block: Block): Boolean {
    val blockData = block.blockData

    if (isSlab(blockData)) {
        val slab = blockData as Slab
        return slab.type == Slab.Type.TOP || slab.type == Slab.Type.DOUBLE
    }

    if (isStair(blockData)) {
        val stair = blockData as Stairs
        return stair.half == Bisected.Half.TOP
    }

    return true
}

private fun offsetLocation(location: Location): Location {
    val block = location.block
    val loc = location.clone()

    loc.add(0.5, 0.0, 0.5)
    loc.subtract(0.0, 1.0, 0.0)

    if (!treatAsNormal(block)) {
        loc.subtract(0.0, 0.5, 0.0)
    }

    val blockData = block.blockData

    if (isStair(blockData)) {
        return if (treatAsNormal(block)) loc else offsetStair(blockData as Stairs, loc)
    }

    return loc
}

private fun offsetStair(blockData: Stairs, location: Location): Location {
    val loc = location.clone()
    val facing = blockData.facing
    val shape = blockData.shape

    when (facing) {
        BlockFace.NORTH -> loc.add(0.0, 0.0, 0.25)
        BlockFace.SOUTH -> loc.subtract(0.0, 0.0, 0.25)
        BlockFace.WEST -> loc.add(0.25, 0.0, 0.0)
        BlockFace.EAST -> loc.subtract(0.25, 0.0, 0.0)
        else -> {}
    }

    return offsetStairShape(facing, shape, loc)
}

private fun offsetStairShape(facing: BlockFace, shape: Stairs.Shape, location: Location): Location {
    val loc = location.clone()

    when (facing) {
        BlockFace.NORTH -> loc.apply {
            if (shape.name.endsWith("LEFT")) {
                add(0.25, 0.0, 0.0)
            } else if (shape.name.endsWith("RIGHT")) {
                subtract(0.25, 0.0, 0.0)
            }
        }

        BlockFace.SOUTH -> loc.apply {
            if (shape.name.endsWith("LEFT")) {
                subtract(0.25, 0.0, 0.0)
            } else if (shape.name.endsWith("RIGHT")) {
                add(0.25, 0.0, 0.0)
            }
        }

        BlockFace.WEST -> loc.apply {
            if (shape.name.endsWith("LEFT")) {
                subtract(0.0, 0.0, 0.25)
            } else if (shape.name.endsWith("RIGHT")) {
                add(0.0, 0.0, 0.25)
            }
        }

        BlockFace.EAST -> loc.apply {
            if (shape.name.endsWith("LEFT")) {
                add(0.0, 0.0, 0.25)
            } else if (shape.name.endsWith("RIGHT")) {
                subtract(0.0, 0.0, 0.25)
            }
        }

        else -> {}
    }

    return loc
}

fun checkLocation(location: Location): Boolean {
    val block = location.block
    val blockData = block.blockData

    if (!(block.isSolid || isSlab(blockData) || isSlab(blockData))) {
        return false
    }

    val offset1 = location.clone().add(0.0, 1.0, 0.0).block.type
    val offset2 = location.clone().add(0.0, 2.0, 0.0).block.type

    return offset1 == Material.AIR && offset2 == Material.AIR
}

fun findLegalLocation(startPoint: Location): Location? {
    for (i in 1..10) {
        val location = startPoint.clone().subtract(0.0, i.toDouble(), 0.0)

        if (!checkLocation(location)) {

            /*
            * 可能存在找到实心位置，但是没有空间的情况。
            * 在这种情况下直接判定为不允许坐下，并且不继续寻找下一层以避免穿透的问题。
            * */
            if (location.block.isSolid) {
                return null
            }

            continue
        }

        return location.toBlockLocation()
    }

    return null
}