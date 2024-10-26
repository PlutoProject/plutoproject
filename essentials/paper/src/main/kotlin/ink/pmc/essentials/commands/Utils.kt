package ink.pmc.essentials.commands

import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager

fun parseWarpName(input: String): String {
    return input.substringBefore('-')
}

suspend fun parseWarp(input: String): Warp? {
    return WarpManager.get(parseWarpName(input))
}