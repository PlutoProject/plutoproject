package ink.pmc.framework.interactive.inventory.layout.list

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.components.SeparatePageTuner
import ink.pmc.framework.interactive.inventory.components.SeparatePageTunerMode
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Menu
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.utils.visual.mochaSubtext0
import org.bukkit.Material

abstract class ListMenu<E, M : ListMenuModel<E>>(val options: ListMenuOptions = ListMenuOptions()) : Screen {
    val model: ProvidableCompositionLocal<M> =
        staticCompositionLocalOf { error("Uninitialized") }

    init {
        require(options.rows >= 3) { "Menu must have at least 3 rows" }
    }

    @Composable
    abstract fun modelProvider(): M

    @Composable
    open fun reloadCondition(): Array<Any> {
        val model = model.current
        return arrayOf(model.page)
    }

    @Composable
    @Suppress("UNCHECKED_CAST")
    override fun Content() {
        val modelInstance = modelProvider() as ScreenModel
        val model = rememberScreenModel { modelInstance }
        CompositionLocalProvider(this.model provides model as M) {
            MenuLayout()
        }
    }

    @Composable
    @Suppress("FunctionName")
    abstract fun Element(obj: E)

    @Composable
    @Suppress("FunctionName")
    open fun MenuLayout() {
        Menu(
            title = options.title,
            rows = options.rows,
            topBorder = options.topBorder,
            bottomBorder = options.bottomBorder,
            leftBorder = options.leftBorder,
            rightBorder = options.rightBorder,
            navigatorWarn = options.navigatorWarn,
            bottomBorderAttachment = { BottomBorderAttachment() }
        ) {
            MenuContent()
        }
    }

    @Composable
    @Suppress("FunctionName")
    open fun MenuContent() {
        val model = model.current
        LaunchedEffect(reloadCondition()) {
            model.internalLoadPageContents()
        }
        if (model.isLoading) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                Row(modifier = Modifier.fillMaxWidth().height(2), horizontalArrangement = Arrangement.Center) {
                    Item(
                        material = Material.CHEST_MINECART,
                        name = component {
                            text("正在加载...") with mochaSubtext0 without italic()
                        }
                    )
                }
            }
            return
        }
        if (model.contents.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                Row(modifier = Modifier.fillMaxWidth().height(2), horizontalArrangement = Arrangement.Center) {
                    Item(
                        material = Material.MINECART,
                        name = component {
                            text("这里没有内容 :(") with mochaSubtext0 without italic()
                        }
                    )
                }
            }
            return
        }
        VerticalGrid(modifier = Modifier.fillMaxSize()) {
            model.contents.forEach {
                Element(it)
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    open fun BottomBorderAttachment() {
        if (model.current.isLoading) return
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
            PreviousTurner()
            Spacer(modifier = Modifier.height(1).width(1))
            NextTurner()
        }
    }

    @Composable
    @Suppress("FunctionName")
    open fun PreviousTurner() {
        val model = model.current
        if (model.pageCount <= 1) {
            Spacer(modifier = Modifier.height(1).width(1))
            return
        }
        SeparatePageTuner(
            icon = options.previousTurnerIcon,
            mode = SeparatePageTunerMode.PREVIOUS,
            current = model.page + 1,
            total = model.page,
            turn = model::previousPage
        )
    }

    @Composable
    @Suppress("FunctionName")
    open fun NextTurner() {
        val model = model.current
        if (model.pageCount <= 1) {
            Spacer(modifier = Modifier.height(1).width(1))
            return
        }
        SeparatePageTuner(
            mode = SeparatePageTunerMode.NEXT,
            current = model.page + 1,
            total = model.pageCount,
            turn = model::nextPage
        )
    }
}