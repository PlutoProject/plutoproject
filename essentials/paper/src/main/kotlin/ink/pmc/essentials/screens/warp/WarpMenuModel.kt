package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import ink.pmc.essentials.config.Warp
import ink.pmc.essentials.screens.warp.WarpMenuModel.Filter.*

private const val PAGE_SIZE = 36

class WarpMenuModel : ScreenModel {
    var filter by mutableStateOf(ALL)
    var isLoading by mutableStateOf(true)
    var pageCount by mutableStateOf(-1)
    var currentPage by mutableStateOf(-1)
    var content = mutableStateListOf<Warp>()

    enum class Filter {
        ALL, COLLECTED, MACHINE, ARCHITECTURE, TOWN
    }

    suspend fun loadPage(page: Int) {
        isLoading = true
        pageCount = -1
        currentPage = -1
        content.clear()
        when (filter) {
            ALL -> TODO()
            COLLECTED -> TODO()
            MACHINE -> TODO()
            ARCHITECTURE -> TODO()
            TOWN -> TODO()
        }
    }

    fun nextFilter() {
        filter = when (filter) {
            ALL -> COLLECTED
            COLLECTED -> MACHINE
            MACHINE -> ARCHITECTURE
            ARCHITECTURE -> TOWN
            TOWN -> ALL
        }
    }

    fun previousFilter() {
        filter = when (filter) {
            ALL -> TOWN
            COLLECTED -> ALL
            MACHINE -> COLLECTED
            ARCHITECTURE -> MACHINE
            TOWN -> ARCHITECTURE
        }
    }
}