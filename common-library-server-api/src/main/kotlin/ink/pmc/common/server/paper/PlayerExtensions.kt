package ink.pmc.common.server.paper

import ink.pmc.common.server.player.ServerPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

val ServerPlayer.player: Player
    get() = Bukkit.getPlayer(this.uniqueID)!!