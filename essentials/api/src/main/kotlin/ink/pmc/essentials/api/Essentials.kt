package ink.pmc.essentials.api

import ink.pmc.essentials.api.economy.EconomyManager
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.player.PlayerManager
import ink.pmc.essentials.api.teleport.RandomTeleportManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.warp.WarpManager

@Suppress("UNUSED")
object Essentials : IEssentials by IEssentials.instance

@Suppress("UNUSED")
interface IEssentials {

    companion object {
        lateinit var instance: IEssentials
    }

    val teleportManager: TeleportManager
    val randomTeleportManager: RandomTeleportManager
    val homeManager: HomeManager
    val warpManager: WarpManager
    val playerManager: PlayerManager
    val economyManager: EconomyManager

}