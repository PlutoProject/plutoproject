package ink.pmc.common.utils.proto

import ink.pmc.common.utils.proto.player.PlayerOuterClass
import ink.pmc.common.utils.proto.player.player
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

val Player.proto: PlayerOuterClass.Player
    get() {
        val player = this
        return player {
            username = player.name
            uuid = player.uniqueId.toString()
        }
    }

val PlayerOuterClass.Player.paper: Player?
    get() = Bukkit.getPlayer(UUID.fromString(this.uuid))