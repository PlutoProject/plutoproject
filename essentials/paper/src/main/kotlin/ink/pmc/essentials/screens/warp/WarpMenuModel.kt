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

private const val PAGE_SIZE = 36

class WarpMenuModel(private val player: Player) : ScreenModel {
    private var filter by mutableStateOf(ALL)
    var isLoading by mutableStateOf(true)
    var pageCount by mutableStateOf(-1)
    var page by mutableStateOf(0)
    var contents = mutableStateListOf<Warp>()

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
        require(page in 0 until pageCount || pageCount == -1) { "Page must be in range: [0, $pageCount)" }
        isLoading = true
        pageCount = -1
        contents.clear()
        when (filter) {
            ALL -> {
                pageCount = WarpManager.getPageCount(PAGE_SIZE)
                contents.addAll(WarpManager.listByPage(PAGE_SIZE, page))
            }

            COLLECTED -> {
                pageCount = WarpManager.getCollectionPageCount(player, PAGE_SIZE)
                contents.addAll(WarpManager.getCollectionByPage(player, PAGE_SIZE, page))
            }

            else -> {
                pageCount = WarpManager.getPageCount(PAGE_SIZE, filter.category)
                contents.addAll(WarpManager.listByPage(PAGE_SIZE, page, filter.category))
            }
        }
        isLoading = false
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
    }
}