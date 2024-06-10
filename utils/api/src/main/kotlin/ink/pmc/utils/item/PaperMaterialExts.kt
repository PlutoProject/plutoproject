package ink.pmc.utils.item

import org.bukkit.Material

val Material.keyed: KeyedMaterial
    get() {
        return KeyedMaterial("minecraft", toString().lowercase())
    }