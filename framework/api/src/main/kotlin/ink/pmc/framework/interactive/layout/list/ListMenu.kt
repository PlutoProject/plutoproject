package ink.pmc.framework.interactive.layout.list

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.framework.interactive.components.SeparatePageTuner
import ink.pmc.framework.interactive.components.SeparatePageTunerMode
import ink.pmc.framework.interactive.jetpack.Arrangement
import ink.pmc.framework.interactive.layout.Column
import ink.pmc.framework.interactive.layout.Menu
import ink.pmc.framework.interactive.layout.Row
import ink.pmc.framework.chat.mochaSubtext0
import ink.pmc.framework.interactive.*
import org.bukkit.Material

abstract class ListMenu<E, M : ListMenuModel<E>> : InteractiveScreen() {
    val LocalListMenuModel: ProvidableCompositionLocal<M> =
        staticCompositionLocalOf { error("Uninitialized") }
    val LocalListMenuOptions: ProvidableCompositionLocal<ListMenuOptions> =
        staticCompositionLocalOf { error("Uninitialized") }

    @Composable
    abstract fun modelProvider(): M

    @Composable
    open fun reloadConditionProvider(): Array<Any> {
        val model = LocalListMenuModel.current
        return arrayOf(model.page)
    }

    @Composable
    @Suppress("UNCHECKED_CAST")
    override fun Content() {
        val modelInstance = modelProvider() as ScreenModel
        val model = rememberScreenModel { modelInstance }
        val options = remember { ListMenuOptions() }
        CompositionLocalProvider(
            LocalListMenuModel provides model as M,
            LocalListMenuOptions provides options
        ) {
            MenuLayout()
        }
    }

    @Composable
    @Suppress("FunctionName")
    abstract fun Element(obj: E)

    @Composable
    @Suppress("FunctionName")
    open fun MenuLayout() {
        val options = LocalListMenuOptions.current
        require(options.rows >= 3) { "Menu must have at least 3 rows" }
        Menu(
            title = options.title,
            rows = options.rows,
            topBorder = options.topBorder,
            bottomBorder = options.bottomBorder,
            leftBorder = options.leftBorder,
            rightBorder = options.rightBorder,
            bottomBorderAttachment = { BottomBorderAttachment() },
            background = options.background,
            centerBackground = options.centerBackground
        ) {
            MenuContent()
        }
    }

    @Composable
    @Suppress("FunctionName")
    open fun MenuContent() {
        val model = LocalListMenuModel.current
        LaunchedEffect(*reloadConditionProvider()) {
            model.loadPageContents()
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
        if (LocalListMenuModel.current.isLoading) return
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
            PreviousTurner()
            Spacer(modifier = Modifier.height(1).width(1))
            NextTurner()
        }
    }

    @Composable
    @Suppress("FunctionName")
    open fun PreviousTurner() {
        val model = LocalListMenuModel.current
        val options = LocalListMenuOptions.current
        if (model.pageCount <= 1) {
            Spacer(modifier = Modifier.height(1).width(1))
            return
        }
        SeparatePageTuner(
            icon = options.previousTurnerIcon,
            mode = SeparatePageTunerMode.PREVIOUS,
            current = model.page + 1,
            total = model.pageCount,
            turn = model::previousPage
        )
    }

    @Composable
    @Suppress("FunctionName")
    open fun NextTurner() {
        val model = LocalListMenuModel.current
        val options = LocalListMenuOptions.current
        if (model.pageCount <= 1) {
            Spacer(modifier = Modifier.height(1).width(1))
            return
        }
        SeparatePageTuner(
            icon = options.nextTurnerIcon,
            mode = SeparatePageTunerMode.NEXT,
            current = model.page + 1,
            total = model.pageCount,
            turn = model::nextPage
        )
    }
}