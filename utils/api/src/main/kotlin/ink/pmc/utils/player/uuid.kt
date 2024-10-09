package ink.pmc.utils.player

import org.bukkit.Bukkit
import java.util.UUID

inline val UUID.isBukkitOnline: Boolean
    get() = Bukkit.getPlayer(this) != null