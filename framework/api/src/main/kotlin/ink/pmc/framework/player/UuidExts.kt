package ink.pmc.framework.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

inline val UUID.bukkitPlayer: Player?
    get() = Bukkit.getPlayer(this)

inline val UUID.isBukkitOnline: Boolean
    get() = Bukkit.getPlayer(this) != null