package ink.pmc.utils.multiplaform.item.exts

import ink.pmc.utils.multiplaform.item.KeyedMaterial
import org.bukkit.Material

val Material.keyed: KeyedMaterial
    get() {
        return KeyedMaterial("minecraft", toString().lowercase())
    }