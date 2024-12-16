package ink.pmc.menu.button

import androidx.compose.runtime.Composable
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.essentials.DEFAULT_ECONOMY_SYMBOL
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.Item
import ink.pmc.framework.utils.hook.vaultHook
import ink.pmc.framework.utils.trimmed
import ink.pmc.framework.utils.visual.mochaSubtext0
import ink.pmc.framework.utils.visual.mochaText
import ink.pmc.framework.utils.visual.mochaYellow
import ink.pmc.menu.api.dsl.buttonDescriptor
import org.bukkit.Material

val BALANCE_BUTTON_DESCRIPTOR = buttonDescriptor {
    id = "menu:balance"
}

@Suppress("FunctionName")
@Composable
fun Balance() {
    val player = LocalPlayer.current
    if (vaultHook?.economy == null) {
        NotAvailable(
            material = Material.SUNFLOWER,
            name = component {
                text("货币") with mochaYellow without italic()
            }
        )
        return
    }
    Item(
        material = Material.SUNFLOWER,
        name = component {
            text("货币") with mochaYellow without italic()
        },
        lore = buildList {
            add(component {
                val balance = vaultHook!!.economy!!.getBalance(player).trimmed()
                text("你的余额: ") with mochaSubtext0 without italic()
                text("$balance$DEFAULT_ECONOMY_SYMBOL") with mochaText without italic()
            })
            add(component {
                text("可在「礼记」中到访来获取货币") with mochaSubtext0 without italic()
            })
        }
    )
}