package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import ink.pmc.essentials.screens.warp.WarpMenuModel.Filter.*

class WarpMenuModel : ScreenModel {
    enum class Filter {
        ALL, STARRED, MACHINE, ARCHITECTURE, TOWN
    }

    fun nextFilter() {
        filter = when (filter) {
            ALL -> STARRED
            STARRED -> MACHINE
            MACHINE -> ARCHITECTURE
            ARCHITECTURE -> TOWN
            TOWN -> ALL
        }
    }

    fun previousFilter() {
        filter = when (filter) {
            ALL -> TOWN
            STARRED -> ALL
            MACHINE -> STARRED
            ARCHITECTURE -> MACHINE
            TOWN -> ARCHITECTURE
        }
    }

    var filter by mutableStateOf(ALL)
}