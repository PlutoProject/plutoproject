package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.mutableStateListOf
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpCategory
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.interactive.inventory.layout.list.FilterListMenuModel
import org.bukkit.entity.Player

enum class WarpFilter(val filterName: String) {
    ALL("全部"),
    COLLECTED("已收藏"),
    MACHINE("仅看机械类"),
    ARCHITECTURE("仅看建筑类"),
    TOWN("仅看城镇类");

    val category: WarpCategory
        get() = when (this) {
            MACHINE -> WarpCategory.MACHINE
            ARCHITECTURE -> WarpCategory.ARCHITECTURE
            TOWN -> WarpCategory.TOWN
            else -> error("Unexpected")
        }
}

private const val PAGE_SIZE = 28

class WarpMenuModel(private val player: Player) : FilterListMenuModel<Warp, WarpFilter>(WarpFilter.entries) {
    val collected = mutableStateListOf<Warp>()

    override suspend fun loadPageContents() {
        collected.clear()
        when (filter) {
            WarpFilter.ALL -> {
                pageCount = WarpManager.getPageCount(PAGE_SIZE)
                contents.addAll(WarpManager.listByPage(PAGE_SIZE, page))
                collected.addAll(contents.filter { WarpManager.getCollection(player).contains(it) })
            }

            WarpFilter.COLLECTED -> {
                pageCount = WarpManager.getCollectionPageCount(player, PAGE_SIZE)
                contents.addAll(WarpManager.getCollectionByPage(player, PAGE_SIZE, page))
                collected.addAll(contents)
            }

            else -> {
                pageCount = WarpManager.getPageCount(PAGE_SIZE, filter.category)
                contents.addAll(WarpManager.listByPage(PAGE_SIZE, page, filter.category))
                collected.addAll(contents.filter { WarpManager.getCollection(player).contains(it) })
            }
        }
    }
}