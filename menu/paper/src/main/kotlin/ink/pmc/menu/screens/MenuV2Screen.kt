package ink.pmc.menu.screens

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.framework.interactive.InteractiveScreen
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Menu
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.utils.chat.UI_PAGING_SOUND
import ink.pmc.framework.utils.visual.mochaLavender
import ink.pmc.framework.utils.visual.mochaText
import ink.pmc.menu.Button
import ink.pmc.menu.MenuConfig
import ink.pmc.menu.MenuScopeImpl
import ink.pmc.menu.Page
import ink.pmc.menu.api.MenuScope
import ink.pmc.menu.api.MenuScreenModel
import ink.pmc.menu.api.MenuService
import ink.pmc.menu.api.descriptors.PageDescriptor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.inventory.ClickType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MenuV2Screen : InteractiveScreen(), KoinComponent {
    private val config by inject<MenuConfig>()
    private val localScreenModel: ProvidableCompositionLocal<MenuScreenModel> =
        staticCompositionLocalOf { error("Unexpected") }
    private val localMenuScope: ProvidableCompositionLocal<MenuScope> =
        staticCompositionLocalOf { error("Unexpected") }

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { MenuV2ScreenModel() }
        val menuScope = remember { MenuScopeImpl(screenModel) }
        CompositionLocalProvider(
            localScreenModel provides screenModel,
            localMenuScope provides menuScope
        ) {
            Menu(
                title = Component.text("手账"),
                rows = 5,
                topBorderAttachment = {
                    ItemSpacer()
                    val pages = MenuService.pages.take(7)
                    val canAddCap = pages.size in 2..4
                    pages.forEachIndexed { i, e ->
                        Paging(e)
                        if (canAddCap && i != pages.lastIndex) {
                            ItemSpacer()
                        }
                    }
                    ItemSpacer()
                }
            ) {
                val currentPageId = screenModel.currentPageId
                val currentPage = remember(currentPageId) {
                    MenuService.getPageDescriptor(currentPageId)
                        ?: error("PageDescriptor with id $currentPageId not registered")
                }
                Page(currentPage)
            }
        }
    }

    @Suppress("FunctionName")
    @Composable
    private fun Paging(descriptor: PageDescriptor) {
        val screenModel = localScreenModel.current
        val menuScope = localMenuScope.current
        val customButtonId = descriptor.customPagingButtonId
        if (customButtonId != null) {
            val button = MenuService.getButtonDescriptor(customButtonId)
                ?: error("Custom page button with id $customButtonId not registered")
            val buttonComponent = MenuService.getButton(button)
                ?: error("Unexpected")
            menuScope.buttonComponent()
            return
        }
        val player = LocalPlayer.current
        Item(
            material = descriptor.icon,
            name = descriptor.name.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE),
            lore = buildList {
                addAll(descriptor.description.map { desc ->
                    desc.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                })
                add(Component.empty())
                add(component {
                    text("左键 ") with mochaLavender without italic()
                    text("切换至此页面") with mochaText without italic()
                })
            },
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                screenModel.currentPageId = descriptor.id
                player.playSound(UI_PAGING_SOUND)
            }
        )
    }

    @Suppress("FunctionName")
    @Composable
    private fun Page(descriptor: PageDescriptor) {
        Column(modifier = Modifier.fillMaxSize()) {
            val page = remember(descriptor) {
                config.pages.firstOrNull { it.id == descriptor.id }
                    ?: error("Page with id ${descriptor.id} not found in config")
            }
            page.patterns.forEach {
                Line(it, page)
            }
        }
    }

    @Suppress("FunctionName")
    @Composable
    private fun Line(patterns: String, page: Page) {
        Row(modifier = Modifier.fillMaxWidth().height(1)) {
            patterns.forEach { pattern ->
                if (pattern.isWhitespace()) {
                    ItemSpacer()
                    return@forEach
                }
                val button = page.buttons.firstOrNull { it.pattern == pattern }
                    ?: error("Button with pattern $pattern not found in config")
                Button(button)
            }
        }
    }

    @Suppress("FunctionName")
    @Composable
    private fun Button(button: Button) {
        val menuScope = localMenuScope.current
        val descriptor = remember(button) {
            MenuService.getButtonDescriptor(button.id)
                ?: error("ButtonDescriptor with id ${button.id} not registered")
        }
        val buttonComponent = remember(button) {
            MenuService.getButton(descriptor) ?: error("Unexpected")
        }
        menuScope.buttonComponent()
    }
}