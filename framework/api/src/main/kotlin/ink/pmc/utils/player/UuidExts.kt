package ink.pmc.utils.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

inline val UUID.bukkitPlayer: Player?
    get() = Bukkit.getPlayer(this)

inline val UUID.isBukkitOnline: Boolean
    get() = Bukkit.getPlayer(this) != null