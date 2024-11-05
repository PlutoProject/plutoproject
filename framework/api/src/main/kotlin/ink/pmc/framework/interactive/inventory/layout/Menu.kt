package ink.pmc.framework.interactive.inventory.layout

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import ink.pmc.framework.interactive.ComposableFunction
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.components.canvases.Chest
import ink.pmc.framework.interactive.inventory.jetpack.Alignment
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import net.kyori.adventure.text.Component

@Suppress("FunctionName")
@Composable
fun Menu(
    title: Component = Component.empty(),
    rows: Int = 5,
    topBorder: Boolean = true,
    bottomBorder: Boolean = true,
    leftBorder: Boolean = true,
    rightBorder: Boolean = true,
    topBorderAttachment: ComposableFunction = { if (LocalNavigator.current != null) Back() },
    bottomBorderAttachment: ComposableFunction = {},
    leftBorderAttachment: ComposableFunction = {},
    rightBorderAttachment: ComposableFunction = {},
    contents: ComposableFunction
) {
    require(rows in 2..6) { "Row count must be in range: [2, 6]" }
    Chest(title = title) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Top) {
            // Top
            if (topBorder) Box(modifier = Modifier.fillMaxWidth().height(1)) {
                HorizontalBorder()
                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Start) {
                    topBorderAttachment()
                }
            }
            val height = if (topBorder && bottomBorder) rows - 2
            else if (!topBorder && !bottomBorder) rows - 1
            else rows
            if (rows >= 3) Column(
                modifier = Modifier.fillMaxWidth().height(height)
            ) {
                // Left
                if (leftBorder) Box(modifier = Modifier.fillMaxHeight().width(1)) {
                    VerticalBorder(height)
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
                        leftBorderAttachment()
                    }
                }
                // Contents
                HorizontalGrid(
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
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
                        rightBorderAttachment()
                    }
                }
            }
            // Bottom
            if (bottomBorder) Box(modifier = Modifier.fillMaxWidth().height(1)) {
                HorizontalBorder()
                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Start) {
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
    Row(modifier = Modifier.fillMaxSize()) {
        repeat(height) {
            Placeholder()
        }
    }
}