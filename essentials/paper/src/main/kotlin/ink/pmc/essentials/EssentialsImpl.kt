package ink.pmc.essentials

import ink.pmc.essentials.api.IEssentials
import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.economy.EconomyManager
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.player.PlayerManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.api.warp.WarpManager

class EssentialsImpl : IEssentials {

    override val teleportManager: TeleportManager?
        get() = TODO("Not yet implemented")
    override val backManager: BackManager?
        get() = TODO("Not yet implemented")
    override val randomTeleportManager: RandomTeleportManager?
        get() = TODO("Not yet implemented")
    override val homeManager: HomeManager?
        get() = TODO("Not yet implemented")
    override val warpManager: WarpManager?
        get() = TODO("Not yet implemented")
    override val playerManager: PlayerManager?
        get() = TODO("Not yet implemented")
    override val economyManager: EconomyManager?
        get() = TODO("Not yet implemented")

}