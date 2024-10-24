package ink.pmc.transfer.backend.lobby.portal

import ink.pmc.advkt.sound.*
import ink.pmc.framework.utils.world.maxLocation
import ink.pmc.framework.utils.world.minLocation
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.Orientable
import org.bukkit.entity.Player

class PortalView(val player: Player, private val meta: PortalMeta) {

    private var state = State.OFF
    private val portalArea = getPortalArea()
    private val portalDestroySound = sound {
        key(Key.key("block.glass.break"))
        source(Sound.Source.BLOCK)
        volume(1F)
        pitch(0.8F)
    }
    private val portalBlockData = (Material.NETHER_PORTAL.createBlockData() as Orientable).apply {
        axis = meta.axis
    }

    enum class State {
        ON, OFF, DESTORYED
    }

    fun on() {
        state = State.ON
        update()
    }

    fun off() {
        state = State.OFF
        update()
    }

    fun toggle() {
        if (state == State.ON) {
            off()
        } else {
            on()
        }
    }

    private fun getPortalArea(): Set<Location> {
        val set = mutableSetOf<Location>()
        val min = minLocation(meta.a, meta.b)
        val max = maxLocation(meta.a, meta.b)

        for (i in min.blockX..max.blockX) {
            for (j in min.blockY..max.blockY) {
                for (k in min.blockZ..max.blockZ) {
                    set.add(Location(min.world, i.toDouble(), j.toDouble(), k.toDouble()))
                }
            }
        }

        return set
    }

    private fun sendPlace() {
        portalArea.forEach {
            player.sendBlockChange(it, portalBlockData)
        }
    }

    private fun sendRemove() {
        portalArea.forEach {
            player.sendBlockChange(it, Material.AIR.createBlockData())
        }
    }

    private fun sendDestroy() {
        sendPlace()
        sendRemove()
        portalArea.forEach {
            player.spawnParticle(Particle.BLOCK, it.toCenterLocation(), 1, portalBlockData)
            player.playSound(portalDestroySound, it.x, it.y, it.z)
        }
    }

    fun update() {
        when (state) {
            State.ON -> sendPlace()
            State.OFF -> sendRemove()
            State.DESTORYED -> {}
        }
    }

    fun playDestroyAnimation() {
        sendDestroy()
    }

    fun destroy() {
        off()
        state = State.DESTORYED
    }

}