package ink.pmc.framework.datastructure

import net.minecraft.resources.ResourceLocation

@Suppress("NOTHING_TO_INLINE")
inline fun namespacedKey(namespace: String, key: String): ResourceLocation {
    return ResourceLocation.fromNamespaceAndPath(namespace, key)
}

fun namespacedKey(value: String): ResourceLocation {
    val namespace = value.substringBefore(':')
    val key = value.substringAfter(':')
    return namespacedKey(namespace, key)
}