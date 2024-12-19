package ink.pmc.framework.item

import org.bukkit.Material

val KeyedMaterial.bukkit: Material
    get() {
        return Material.getMaterial(key.uppercase()) ?: throw IllegalStateException("Invalid material: $namespace:$key")
    }