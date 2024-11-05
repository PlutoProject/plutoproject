package ink.pmc.framework.interactive.inventory.layout

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.interactive.ComposableFunction
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.components.canvases.Chest
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.utils.visual.mochaMaroon
import ink.pmc.framework.utils.visual.mochaSubtext0
import net.kyori.adventure.text.Component
import java.util.logging.Level

@Suppress("FunctionName")
@Composable
fun Menu(
    title: Component = Component.empty(),
    rows: Int = 5,
    topBorder: Boolean = true,
    bottomBorder: Boolean = true,
    leftBorder: Boolean = true,
    rightBorder: Boolean = true,
    topBorderAttachment: ComposableFunction = { Back() },
    bottomBorderAttachment: ComposableFunction = {},
    leftBorderAttachment: ComposableFunction = {},
    rightBorderAttachment: ComposableFunction = {},
    navigatorWarn: Boolean = true,
    contents: ComposableFunction
) {
    require(rows in 2..6) { "Row count must be in range: [2, 6]" }
    if (LocalNavigator.current == null && navigatorWarn) {
        frameworkLogger.log(Level.WARNING, "A menu layout was opened without Navigator context", IllegalStateException())
        LocalPlayer.current.send {
            text("你打开了一个没有 Navigator 上下文的菜单布局") with mochaMaroon
            newline()
            text("这可能会导致一些问题，请将其报告给管理组") with mochaSubtext0
        }
    }
    Chest(title = title, modifier = Modifier.size(width = 9, height = rows)) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
            // Top
            if (topBorder) Box(modifier = Modifier.fillMaxWidth().height(1)) {
                HorizontalBorder()
                Row(modifier = Modifier.fillMaxSize()) {
                    topBorderAttachment()
                }
            }
            val height = if (topBorder && bottomBorder) rows - 2
            else if (!topBorder && !bottomBorder) rows - 1
            else rows
            if (height >= 1) Row(
                modifier = Modifier.fillMaxWidth().height(height)
            ) {
                // Left
                if (leftBorder) Box(modifier = Modifier.fillMaxHeight().width(1)) {
                    VerticalBorder(height)
                    Column(modifier = Modifier.fillMaxSize()) {
                        leftBorderAttachment()
                    }
                }
                // Contents
                VerticalGrid(
                    modifier = Modifier.fillMaxHeight().width(
                        if (leftBorder && rightBorder) 7
                        else if (!leftBorder && !rightBorder) 9
                        else 8
                    )
                ) {
                    contents()
                }
                // Right
                if (rightBorder) Box(modifier = Modifier.fillMaxHeight().width(1)) {
                    VerticalBorder(height)
                    Column(modifier = Modifier.fillMaxSize()) {
                        rightBorderAttachment()
                    }
                }
            }
            // Bottom
            if (bottomBorder) Box(modifier = Modifier.fillMaxWidth().height(1)) {
                HorizontalBorder()
                Row(modifier = Modifier.fillMaxSize()) {
                    bottomBorderAttachment()
                }
            }
        }
    }
}

@Suppress("FunctionName")
@Composable
private fun HorizontalBorder() {
    Row(modifier = Modifier.fillMaxSize()) {
        repeat(9) {
            Placeholder()
        }
    }
}

@Suppress("FunctionName")
@Composable
private fun VerticalBorder(height: Int) {
    Column(modifier = Modifier.fillMaxSize()) {
        repeat(height) {
            Placeholder()
        }
    }
}