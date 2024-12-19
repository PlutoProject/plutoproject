package ink.pmc.framework.entity

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import java.util.*

inline val UUID.entity: Entity?
    get() = Bukkit.getEntity(this)