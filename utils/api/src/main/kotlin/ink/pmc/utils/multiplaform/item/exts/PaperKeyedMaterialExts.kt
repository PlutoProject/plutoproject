package ink.pmc.utils.multiplaform.item.exts

import ink.pmc.utils.multiplaform.item.KeyedMaterial
import org.bukkit.Material

val KeyedMaterial.bukkit: Material
    get() {
        return Material.getMaterial(key.uppercase()) ?: throw IllegalStateException("Invalid material: $namespace:$key")
    }