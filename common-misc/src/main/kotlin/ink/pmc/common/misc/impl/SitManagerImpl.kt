package ink.pmc.common.misc.impl

import ink.pmc.common.misc.ILLEGAL_LOC
import ink.pmc.common.misc.STAND_UP
import ink.pmc.common.misc.api.isSitting
import ink.pmc.common.misc.api.sit
import ink.pmc.common.misc.api.sit.SitManager
import ink.pmc.common.misc.api.stand
import ink.pmc.common.misc.disabled
import ink.pmc.common.misc.plugin
import ink.pmc.common.utils.execute
import ink.pmc.common.utils.regionScheduler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.time.Duration

/*
* 有时候在尝试坐下的时候会由于未知原因直接触发 EntityDismountEvent，导致无法坐下。
* 猜测可能是和碰撞有关。设置一个延迟机制，来解决这个问题。
* */
val sitDelay = CopyOnWriteArraySet<UUID>()

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

private fun checkLocation(location: Location, checkType: Boolean = true): Boolean {
    return ((location.block.blockData is Slab || location.block.blockData is Stairs || location.block.isSolid) || checkType)
            && location.clone().add(0.0, 1.0, 0.0).block.type == Material.AIR
            && location.clone().add(0.0, 2.0, 0.0).block.type == Material.AIR
}

private fun findLegalLocation(startPoint: Location): Location? {
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

@OptIn(DelicateCoroutinesApi::class)
class SitManagerImpl : SitManager {

    private val _sitter = mutableMapOf<UUID, Location>()
    private val armorStands = mutableMapOf<UUID, UUID>()

    override val sitters: Map<UUID, Location> = _sitter

    init {
        GlobalScope.launch {
            while (!disabled) {
                _sitter.keys.forEach {
                    val player = plugin.server.getPlayer(it)
                    player!!.sendActionBar(STAND_UP)

                    // 避免异步实体获取问题
                    regionScheduler(plugin, player.location) {
                        val playerId = player.uniqueId
                        val armorStandId = armorStands[playerId]!!
                        val armorStand = player.world.getEntity(armorStandId)!!

                        // 切换到实体调度器执行，因为不允许异步操作实体数据
                        armorStand.execute(plugin) {
                            // 避免一些意外问题
                            if (!armorStand.passengers.contains(player)) {
                                armorStand.addPassenger(player)
                            }
                        }
                    }
                }
                delay(Duration.parse("2s"))
            }
        }
    }

    override fun sit(player: Player, location: Location) {
        if (player.isSitting) {
            player.stand()
        }

        var sitLoc = location

        if (!checkLocation(location)) {
            val tryFind = findLegalLocation(location)

            if (tryFind == null) {
                player.sendMessage(ILLEGAL_LOC)
                return
            }

            sitLoc = tryFind
        }

        val blockData = location.block.blockData
        var align = true

        if (blockData is Stairs) {
            val half = blockData.half

            if (half == Bisected.Half.BOTTOM) {
                sitLoc.subtract(0.0, 0.5, 0.0)
                val facing = blockData.facing

                when (facing) { // 台
                    BlockFace.NORTH -> location.apply {
                        add(0.5, 0.0, 0.75)
                    }

                    BlockFace.SOUTH -> location.apply {
                        add(0.5, 0.0, 0.25)
                    }

                    BlockFace.EAST -> sitLoc.apply {
                        add(0.25, 0.0, 0.5)
                    }

                    BlockFace.WEST -> sitLoc.apply {
                        add(0.75, 0.0, 0.5)
                    }

                    else -> return
                }

                align = false
            }
        }

        println(sitLoc)

        val armorStand = createArmorStand(sitLoc, align)
        armorStand.addPassenger(player)

        _sitter[player.uniqueId] = sitLoc
        armorStands[player.uniqueId] = armorStand.uniqueId
        markDelay(player)
        player.sendActionBar(STAND_UP) // 先直接发一次，尝试覆盖原版的载具提示
    }

    override fun isSitting(player: Player): Boolean {
        return _sitter.containsKey(player.uniqueId)
    }

    override fun stand(player: Player) {
        if (!isSitting(player)) {
            return
        }

        val playerId = player.uniqueId
        val armorStandId = armorStands[playerId]!!
        val armorStand = player.world.getEntity(armorStandId)!!
        val standLocation = player.location.clone().add(0.0, 1.0, 0.0)

        // 使用实体调度器，避免未来在迁移 Folia 时可能造成的问题
        armorStand.execute(plugin) {
            armorStand.removePassenger(player)
        }

        player.teleportAsync(standLocation) // 显式异步传送，同上
        player.sendActionBar(Component.text(" "))

        _sitter.remove(playerId)
        cleanArmorStand(armorStandId)
    }

    override fun standAll() {
        _sitter.keys.forEach {
            val player = plugin.server.getPlayer(it)
            player!!.stand()
        }
    }

    private fun createArmorStand(locationToSit: Location, align: Boolean = true): Entity {
        /*
        * TODO: 替换为能在 Folia 上使用的 API
        * 由于 Folia 目前似乎暂无完整的世界操作 API，所以先搁置
        * */
        var armorStandLoc = locationToSit

        if (align) {
            armorStandLoc = locationToSit.toBlockLocation()
            armorStandLoc.x = armorStandLoc.blockX + 0.5
            armorStandLoc.z = armorStandLoc.blockZ + 0.5
        }

        armorStandLoc.subtract(0.0, 1.0, 0.0)

        val world = locationToSit.world
        val entity = world.spawn(armorStandLoc, ArmorStand::class.java)

        entity.setGravity(false)
        entity.isInvisible = true

        return entity
    }

    private fun cleanArmorStand(uuid: UUID) {
        if (!armorStands.containsValue(uuid)) {
            return
        }

        val armorStand = plugin.server.getEntity(uuid)!! as LivingEntity
        armorStand.remove()

        armorStands.remove(uuid)
    }

    private fun markDelay(player: Player) {
        sitDelay.add(player.uniqueId)
        GlobalScope.launch {
            delay(100)
            sitDelay.remove(player.uniqueId)
        }
    }

}