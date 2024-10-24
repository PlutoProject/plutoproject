package ink.pmc.essentials.api

import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.utils.inject.inlinedGet

@Suppress("UNUSED")
interface Essentials {

    companion object : Essentials by inlinedGet()

    val teleportManager: TeleportManager
    val backManager: BackManager
    val randomTeleportManager: RandomTeleportManager
    val homeManager: HomeManager
    val warpManager: WarpManager
    val afkManager: AfkManager

    fun isTeleportEnabled(): Boolean

    fun isRandomTeleportEnabled(): Boolean

    fun isHomeEnabled(): Boolean

    fun isWarpEnabled(): Boolean

    fun isBackEnabled(): Boolean

    fun isAfkEnabled(): Boolean

    fun isItemFrameEnabled(): Boolean

    fun isLecternEnabled(): Boolean

    fun isActionEnabled(): Boolean

    fun isItemEnabled(): Boolean

    fun isRecipeEnabled(): Boolean

    fun isJoinEnabled(): Boolean

}