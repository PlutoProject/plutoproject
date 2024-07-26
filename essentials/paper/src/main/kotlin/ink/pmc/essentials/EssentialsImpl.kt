package ink.pmc.essentials

import ink.pmc.essentials.api.IEssentials
import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.economy.EconomyManager
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.api.warp.WarpManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED")
class EssentialsImpl : IEssentials, KoinComponent {

    private val config by inject<EssentialsConfig>()
    override val teleportManager by inject<TeleportManager>()
    override val backManager by inject<BackManager>()
    override val randomTeleportManager by inject<RandomTeleportManager>()
    override val homeManager by inject<HomeManager>()
    override val warpManager by inject<WarpManager>()
    override val economyManager by inject<EconomyManager>()

    override fun isTeleportManagerEnabled(): Boolean {
        return config.Teleport().enabled
    }

}