package ink.pmc.common.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
val UUID.player: Player?
    get() = Bukkit.getPlayer(this)

@Suppress("UNUSED")
val UUID.entity: Entity?
    get() = Bukkit.getEntity(this)

@Suppress("UNUSED")
val UUID.trimmed: String
    get() = this.toString().replace("-", "")