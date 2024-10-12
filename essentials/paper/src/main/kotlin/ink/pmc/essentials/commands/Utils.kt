package ink.pmc.essentials.commands

import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.api.warp.Warp

fun parseWarpName(input: String): String {
    return input.substringBefore('-')
}

suspend fun parseWarp(input: String): Warp? {
    return Essentials.warpManager.get(parseWarpName(input))
}