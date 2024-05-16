package ink.pmc.common.utils.proto

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.utils.platform.proxy
import ink.pmc.common.utils.proto.player.PlayerOuterClass
import ink.pmc.common.utils.proto.player.player
import java.util.*
import kotlin.jvm.optionals.getOrNull

val Player.proto: PlayerOuterClass.Player
    get() {
        val player = this
        return player {
            username = player.username
            uuid = player.uniqueId.toString()
        }
    }

val PlayerOuterClass.Player.velocity: Player?
    get() = proxy.getPlayer(UUID.fromString(this.uuid)).getOrNull()