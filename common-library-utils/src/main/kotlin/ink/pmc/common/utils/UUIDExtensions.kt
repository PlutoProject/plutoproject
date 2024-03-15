package ink.pmc.common.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

val UUID.player: Player?
    get() = Bukkit.getPlayer(this)

val UUID.entity: Entity?
    get() = Bukkit.getEntity(this)