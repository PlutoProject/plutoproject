package ink.pmc.essentials.commands

import ink.pmc.essentials.Cm
import ink.pmc.essentials.Command
import ink.pmc.utils.dsl.cloud.invoke

@Command("home")
@Suppress("UNUSED")
fun Cm.home(aliases: Array<String>) {
    this("home", *aliases) {

    }
}