package ink.pmc.essentials.commands.warp

import ink.pmc.essentials.Cm
import ink.pmc.essentials.Command
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.incendo.cloud.parser.standard.StringParser

@Command("warp")
@Suppress("UNUSED")
fun Cm.warp(aliases: Array<String>) {
    this("warp", *aliases) {
        permission("essentials.warp")
        required("name", StringParser.stringParser())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.warpManager
            }
        }
    }
}