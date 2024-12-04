package ink.pmc.essentials.screens.home

import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.framework.interactive.inventory.layout.list.ListMenuModel
import org.bukkit.OfflinePlayer
import kotlin.math.ceil

private const val PAGE_SIZE = 28

class HomeViewerScreenModel(private val viewing: OfflinePlayer) : ListMenuModel<Home>() {
    override suspend fun fetchPageContents(): List<Home> {
        val homes = HomeManager.list(viewing).toList()
            .sortedByDescending { it.createdAt }
            .sortedBy {
                if (it.isStarred || it.isPreferred) 0 else 1
            }
        pageCount = ceil(homes.size.toDouble() / PAGE_SIZE).toInt()
        return homes.drop(page * PAGE_SIZE).take(PAGE_SIZE)
    }
}