package ink.pmc.framework.item

import org.bukkit.Material

inline val Material.keyed: KeyedMaterial
    get() {
        return KeyedMaterial("minecraft", toString().lowercase())
    }