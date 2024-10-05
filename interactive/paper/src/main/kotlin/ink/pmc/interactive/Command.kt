package ink.pmc.interactive

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.Gui
import ink.pmc.interactive.examples.ExampleScreen1
import ink.pmc.interactive.examples.ExampleScreen2
import ink.pmc.interactive.examples.ExampleScreen3
import ink.pmc.utils.PaperCm
import ink.pmc.utils.PaperCtx
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.bukkit.entity.Player

private const val PERMISSION = "interactive.example"

fun PaperCm.interactive(alias: Array<String>) {
    this("interactive", *alias) {
        permission(PERMISSION)
        "example_1" {
            permission(PERMISSION)
            handler {
                startInventory {
                    Navigator(ExampleScreen1())
                }
            }
        }

        "example_2" {
            permission(PERMISSION)
            handler {
                startInventory {
                    Navigator(ExampleScreen2())
                }
            }
        }

        "example_3" {
            permission(PERMISSION)
            handler {
                startInventory {
                    ExampleScreen3()
                }
            }
        }
    }
}

private fun PaperCtx.startInventory(content: ComposableFunction) {
    val sender = sender.sender
    if (sender !is Player) {
        sender.sendMessage(NON_PLAYER)
        return
    }
    Gui.startInventory(sender) {
        content()
    }
}