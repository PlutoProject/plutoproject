package ink.pmc.essentials

import ink.pmc.essentials.afk.AfkManagerImpl
import ink.pmc.essentials.api.IEssentials
import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.back.BackManagerImpl
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.home.HomeManagerImpl
import ink.pmc.essentials.repositories.BackRepository
import ink.pmc.essentials.repositories.HomeRepository
import ink.pmc.essentials.repositories.WarpRepository
import ink.pmc.essentials.teleport.TeleportManagerImpl
import ink.pmc.essentials.teleport.random.RandomTeleportManagerImpl
import ink.pmc.essentials.warp.WarpManagerImpl
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

private val ess by lazy { getKoin().get<IEssentials>() }

val appModule = module {
    single { EssentialsConfig(fileConfig) }
    single<IEssentials> { EssentialsImpl() }
    single<HomeRepository> { HomeRepository() }
    single<WarpRepository> { WarpRepository() }
    single<BackRepository> { BackRepository() }
    single<TeleportManager> {
        require(ess.isTeleportEnabled()) { "TeleportManager not available" }
        TeleportManagerImpl()
    }
    single<RandomTeleportManager> {
        require(ess.isRandomTeleportEnabled()) { "RandomTeleportManager not available" }
        RandomTeleportManagerImpl()
    }
    single<HomeManager> {
        require(ess.isHomeEnabled()) { "HomeManager not available" }
        HomeManagerImpl()
    }
    single<WarpManager> {
        require(ess.isWarpEnabled()) { "WarpManager not available" }
        WarpManagerImpl()
    }
    single<BackManager> {
        require(ess.isBackEnabled()) { "BackManager not available" }
        BackManagerImpl()
    }
    single<AfkManager> {
        require(ess.isAfkEnabled()) { "AfkManager not available" }
        AfkManagerImpl()
    }
}