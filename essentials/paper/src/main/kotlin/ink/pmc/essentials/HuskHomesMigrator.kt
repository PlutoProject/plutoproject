package ink.pmc.essentials

import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.utils.platform.paper
import kotlinx.coroutines.future.await
import org.bukkit.Bukkit
import org.bukkit.Location
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.jvm.optionals.getOrNull

object HuskHomesMigrator : KoinComponent {

    private val homeManager by inject<HomeManager>()
    private val warpManager by inject<WarpManager>()
    private val huskHomesApi = huskHomesHook?.huskHomesApi
    private val logger = plugin.logger

    suspend fun migrateWarps() {
        if (huskHomesApi == null) return

        logger.info("Fetching warp list...")
        val warps = huskHomesApi.warps.await()
        logger.info("Warp list fetched, start migrating...")

        warps.forEach {
            val name = it.name
            val location = Bukkit.getWorld(it.world.name)?.let { w ->
                Location(w, it.x, it.y, it.z, it.yaw, it.pitch)
            }

            if (location == null) {
                logger.warning("World ${it.world.name} not found, cannot migrate")
                return@forEach
            }

            warpManager.create(name, location)
            logger.info("Warp '$name' migrated")
        }

        logger.info("Done!")
    }

    suspend fun migrateHomes() {
        if (huskHomesApi == null) return

        val players = paper.offlinePlayers
        logger.info("Total ${players.size} local players")

        players.forEach {
            val user = huskHomesApi.getUserData(it.uniqueId).await().getOrNull()?.user ?: return@forEach
            val homes = huskHomesApi.getUserHomes(user).await()
            logger.info("Fetched home list of ${it.name}, start migrating...")
            homes.forEach homes@{ h ->
                val name = h.name
                val location = it.location

                if (location == null) {
                    logger.warning("Failed to migrate '$name' of ${it.name} because world is null")
                    return@homes
                }

                homeManager.create(it, name, location)
                logger.info("Home '$name' of ${it.name} migrated")
            }
        }

        logger.info("Done!")
    }

}