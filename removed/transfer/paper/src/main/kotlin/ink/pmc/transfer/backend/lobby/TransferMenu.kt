package ink.pmc.transfer.backend.lobby

import ink.pmc.advkt.component.empty
import ink.pmc.transfer.AbstractTransferService
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.scripting.Menu
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.framework.utils.dsl.invui.gui.GuiDsl
import ink.pmc.framework.utils.dsl.invui.gui.gui
import ink.pmc.framework.utils.dsl.invui.item.SuspendClickHandler
import ink.pmc.framework.utils.dsl.invui.item.simpleItem
import ink.pmc.framework.utils.dsl.invui.window.WindowDsl
import ink.pmc.framework.utils.dsl.invui.window.singleWindow
import ink.pmc.framework.utils.multiplaform.item.exts.bukkit
import ink.pmc.framework.utils.multiplaform.player.paper.wrapped
import ink.pmc.framework.utils.visual.mochaSubtext0
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import xyz.xenondevs.invui.window.Window

class TransferMenu(
    private val service: AbstractTransferService,
    private val lobby: TransferLobby,
    private val main: Menu,
    private val categories: Map<String, Menu>
) {

    private fun GuiDsl<*>.background(menu: Menu) {
        if (menu.background == null) {
            return
        }

        ingredient(menu.background, Material.GRAY_STAINED_GLASS_PANE) {
            displayName { empty() }
        }
    }

    private fun WindowDsl<*>.closeButton(menu: Menu, scope: GuiDsl<*>) {
        if (menu.closeButton == null) {
            return
        }

        scope.simpleItem(menu.closeButton) {
            provider(Material.RED_STAINED_GLASS_PANE) {
                displayName = MENU_CLOSE
            }
            onClick {
                window.close()
            }
        }
    }

    private suspend fun WindowDsl<*>.backButton(menu: Menu, scope: GuiDsl<*>, action: SuspendClickHandler) {
        if (menu.background == null) {
            return
        }

        scope.simpleItem(menu.background) {
            provider(Material.YELLOW_STAINED_GLASS_PANE) {
                displayName = MENU_BACK
            }
            onClickAsync {
                action(it)
            }
        }
    }

    private suspend fun WindowDsl<*>.destinationButton(menu: Menu, scope: GuiDsl<*>) {
        menu.destination.forEach {
            val destination = service.getDestination(it.key) ?: return@forEach
            scope.simpleItem(it.value) {
                val verifyResult = lobby.verifyCondition(viewer!!, destination)
                provider(destination.icon.bukkit) {
                    displayName = destination.name.removeItalic()
                    lore(destinationStatus(destination))
                    lore { empty() }
                    destination.description.forEach { c ->
                        lore(c.removeItalic())
                    }
                    lore { empty() }
                    lore(destinationJoinPrompt(destination, verifyResult.first, verifyResult.second))
                }
                onClickAsync {
                    if (destination.status != DestinationStatus.ONLINE) {
                        return@onClickAsync
                    }
                    viewer!!.sync { window.close() }
                    lobby.transferPlayer(viewer!!, destination)
                }
            }
        }
    }

    private suspend fun WindowDsl<*>.categoryButton(menu: Menu, scope: GuiDsl<*>) {
        menu.category.forEach {
            val category = service.getCategory(it.key) ?: return@forEach
            scope.simpleItem(it.value) {
                provider(category.icon.bukkit) {
                    displayName = category.name.removeItalic().color(mochaSubtext0)
                    lore { empty() }
                    category.description.forEach { c ->
                        lore(c.removeItalic().color(mochaSubtext0))
                    }
                    lore { empty() }
                    lore(CATEGORY_CLICK_TO_OPEN)
                }
                onClickAsync {
                    // clearHandlers()
                    // viewer!!.sync { categoryGui(category.id) }
                    window.addCloseHandler { menu.closeHandler(viewer!!.wrapped) }
                }
            }
        }
    }

    private fun Component.removeItalic(): Component {
        return decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
    }

    private suspend fun WindowDsl<*>.mainGui() {
        gui {
            structure(*main.structure.toTypedArray())
            background(main)
            closeButton(main, this)
            destinationButton(main, this)
            categoryButton(main, this)
            whenBuild {
                changeTitle(Component.text(RANDOM_MAIN_MENU_TITLE))
            }
        }
    }

    // 之后使用新菜单框架重构
    /*
    private suspend fun WindowDsl<*>.categoryGui(id: String) {
        val menu = categories[id] ?: return
        menu.openHandler(viewer!!.wrapped)
        gui {
            structure(*main.structure.toTypedArray())
            background(main)
            closeButton(main, this)
            backButton(menu, this) {
                clearHandlers()
                mainGui()
            }
            destinationButton(main, this)
            categoryButton(main, this)
        }
    }
     */

    /*
    private fun WindowDsl<*>.clearHandlers() {
        window.setOpenHandlers(mutableListOf())`
        window.setCloseHandlers(mutableListOf())
    }
     */

    suspend fun openWindow(player: Player): Window {
        return singleWindow {
            title { empty() }
            viewer = player

            onClose {
                lobby.handleMenuClose(viewer!!)
            }

            mainGui()
        }.apply {
            open()
        }
    }

}