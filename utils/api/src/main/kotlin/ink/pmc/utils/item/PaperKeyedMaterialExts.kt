package ink.pmc.utils.item

import org.bukkit.Material

val KeyedMaterial.bukkit: Material
    get() {
        return Material.getMaterial(key.uppercase()) ?: throw IllegalStateException("Invalid material: $namespace:$key")
    }