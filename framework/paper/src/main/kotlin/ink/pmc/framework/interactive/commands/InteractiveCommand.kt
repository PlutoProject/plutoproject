package ink.pmc.framework.interactive.commands

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.framework.interactive.examples.ExampleScreen1
import ink.pmc.framework.interactive.examples.ExampleScreen2
import ink.pmc.framework.interactive.examples.ExampleScreen3
import ink.pmc.framework.startInventory
import ink.pmc.framework.command.ensurePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object InteractiveCommand {
    private const val PERMISSION = "interactive.example"

    @Command("interactive example_1")
    @Permission(PERMISSION)
    fun CommandSender.example1() = ensurePlayer {
        startInventory {
            Navigator(ExampleScreen1())
        }
    }

    @Command("interactive example_2")
    @Permission(PERMISSION)
    fun CommandSender.example2() = ensurePlayer {
        startInventory {
            Navigator(ExampleScreen2())
        }
    }

    @Command("interactive example_3")
    @Permission(PERMISSION)
    fun CommandSender.example3() = ensurePlayer {
        startInventory {
            ExampleScreen3()
        }
    }
}