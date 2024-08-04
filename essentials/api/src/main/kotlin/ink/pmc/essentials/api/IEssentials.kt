package ink.pmc.essentials.api

import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.api.warp.WarpManager
import org.koin.java.KoinJavaComponent.getKoin

@Suppress("UNUSED")
object Essentials : IEssentials by IEssentials.instance

@Suppress("UNUSED")
interface IEssentials {

    companion object {
        val instance by lazy { getKoin().get<IEssentials>() }
    }

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

}