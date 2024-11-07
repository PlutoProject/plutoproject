package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpCategory
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.screens.warp.WarpMenuModel.Filter.*
import org.bukkit.entity.Player

internal const val PAGE_SIZE = 28

class WarpMenuModel(private val player: Player) : ScreenModel {
    var filter by mutableStateOf(ALL)
    var isLoading by mutableStateOf(true)
    var pageCount by mutableStateOf(-1)
    var page by mutableStateOf(0)
    val contents = mutableStateListOf<Warp>()
    val collected = mutableStateListOf<Warp>()

    enum class Filter {
        ALL, COLLECTED, MACHINE, ARCHITECTURE, TOWN;

        val category: WarpCategory
            get() = when (this) {
                MACHINE -> WarpCategory.MACHINE
                ARCHITECTURE -> WarpCategory.ARCHITECTURE
                TOWN -> WarpCategory.TOWN
                else -> error("Unexpected")
            }
    }

    suspend fun loadPage() {
        println("loadPage begin")
        isLoading = true
        pageCount = -1
        contents.clear()
        collected.clear()
        when (filter) {
            ALL -> {
                pageCount = WarpManager.getPageCount(PAGE_SIZE)
                contents.addAll(WarpManager.listByPage(PAGE_SIZE, page))
                collected.addAll(contents.filter { WarpManager.getCollection(player).contains(it) })
            }

            COLLECTED -> {
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
        val range = 0 until pageCount
        check(page in range || range.isEmpty()) { "Page $page must be in range: [0, $pageCount)" }
        isLoading = false
        println("loadPage done")
    }

    fun nextFilter() {
        filter = when (filter) {
            ALL -> COLLECTED
            COLLECTED -> MACHINE
            MACHINE -> ARCHITECTURE
            ARCHITECTURE -> TOWN
            TOWN -> ALL
        }
        page = 0
        println("nextFilter: $page, $filter")
    }

    fun previousFilter() {
        filter = when (filter) {
            ALL -> TOWN
            COLLECTED -> ALL
            MACHINE -> COLLECTED
            ARCHITECTURE -> MACHINE
            TOWN -> ARCHITECTURE
        }
        page = 0
        println("previousFilter: $page, $filter")
    }
}