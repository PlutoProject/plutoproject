package ink.pmc.framework.interactive.inventory.layout.list

import androidx.compose.runtime.Composable
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.components.Selector
import ink.pmc.framework.interactive.inventory.fillMaxSize
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.utils.visual.mochaText

abstract class FilterListMenu<E, F : Any, M : FilterListMenuModel<E, F>>(
    options: ListMenuOptions = ListMenuOptions(),
    private val filters: Map<F, String>
) : ListMenu<E, M>(options) {
    @Composable
    override fun BottomBorderAttachment() {
        if (model.current.isLoading) return
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
            PreviousTurner()
            FilterSelector()
            NextTurner()
        }
    }

    override fun reloadConditionProvider(): Array<Any> {
        val model = model.current
        return arrayOf(model.page, model.filter)
    }

    @Composable
    @Suppress("FunctionName")
    open fun FilterSelector() {
        val model = model.current
        Selector(
            title = component {
                text("筛选") with mochaText without italic()
            },
            options = filters.values.toList(),
            goNext = model::internalNextFilter,
            goPrevious = model::internalPreviousFilter
        )
    }
}