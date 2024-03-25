package ink.pmc.common.server.player

import ink.pmc.common.server.Server
import ink.pmc.common.server.entity.EntityStatus
import ink.pmc.common.server.entity.ServerEntity
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title

@Suppress("UNUSED")
interface ServerPlayer : ServerEntity {

    var displayName: Component?
        get() = operator.getDisplayName(this)
        set(value) {
            if (value == null) {
                return
            }

            operator.setDisplayName(this, value)
        }
    override val operator: PlayerOperator
    val isOnline: Boolean
        get() = status != EntityStatus.NON_EXIST

    fun sendMessage(component: Component) {
        operator.sendMessage(this, component)
    }

    fun sendActionbar(component: Component) {
        operator.sendActionbar(this, component)
    }

    fun sendTitle(title: Title) {
        operator.sendTitle(this, title)
    }

    fun playSound(sound: Sound) {
        operator.playSound(this, sound)
    }

    fun switchServer(target: Server) {
        operator.switchServer(this, target)
    }

}