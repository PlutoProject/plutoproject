package ink.pmc.essentials

import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.config.EssentialsConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED")
class EssentialsImpl : Essentials, KoinComponent {

    private val config by inject<EssentialsConfig>()
    override val teleportManager by inject<TeleportManager>()
    override val backManager by inject<BackManager>()
    override val randomTeleportManager by inject<RandomTeleportManager>()
    override val homeManager by inject<HomeManager>()
    override val warpManager by inject<WarpManager>()
    override val afkManager by inject<AfkManager>()

    override fun isTeleportEnabled(): Boolean {
        return config.Teleport().enabled
    }

    override fun isRandomTeleportEnabled(): Boolean {
        return isTeleportEnabled() && config.RandomTeleport().enabled
    }

    override fun isHomeEnabled(): Boolean {
        return isTeleportEnabled() && config.Home().enabled
    }

    override fun isWarpEnabled(): Boolean {
        return isTeleportEnabled() && config.Warp().enabled
    }

    override fun isBackEnabled(): Boolean {
        return isTeleportEnabled() && config.Back().enabled
    }

    override fun isAfkEnabled(): Boolean {
        return config.Afk().enabled
    }

    override fun isItemFrameEnabled(): Boolean {
        return config.ItemFrame().enabled
    }

    override fun isLecternEnabled(): Boolean {
        return config.Lectern().enabled
    }

    override fun isActionEnabled(): Boolean {
        return config.Action().enabled
    }

    override fun isItemEnabled(): Boolean {
        return config.Item().enabled
    }

    override fun isJoinEnabled(): Boolean {
        return config.Join().enabled
    }

}