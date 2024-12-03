package ink.pmc.framework.interactive.inventory.layout.list_menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel

abstract class ListMenuModel<E>(val pageSize: Int) : ScreenModel {
    var isLoading by mutableStateOf(true)
    var pageCount by mutableStateOf(-1)
    var page by mutableStateOf(0)
    val contents = mutableStateListOf<E>()

    abstract suspend fun loadPageContents()

    fun previousPage(): Boolean {
        return if (page > 0) {
            page--
            true
        } else false
    }

    fun nextPage(): Boolean {
        return if (page < pageCount - 1) {
            page++
            true
        } else false
    }
}