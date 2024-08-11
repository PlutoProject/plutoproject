package ink.pmc.essentials.commands

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.COMMAND_ESS_RTP
import ink.pmc.essentials.COMMAND_ESS_RTP_PERF_END
import ink.pmc.essentials.COMMAND_ESS_RTP_PERF_START
import ink.pmc.essentials.Cm
import ink.pmc.essentials.screens.examples.ExampleScreen1
import ink.pmc.essentials.teleport.random.PerfTest
import ink.pmc.interactive.api.Gui
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("essentials")
@Suppress("UNUSED")
fun Cm.essentials(aliases: Array<String>) {
    this("essentials", *aliases) {
        "rtp" {
            permission("essentials.cmd")
            handler {
                sender.sender.sendMessage(COMMAND_ESS_RTP)
            }
        }
        "rtp_perf_test" {
            permission("essentials.cmd")
            handler {
                checkPlayer(sender.sender) {
                    if (!PerfTest.isInTest(this)) {
                        PerfTest.startTest(this)
                        sendMessage(COMMAND_ESS_RTP_PERF_START)
                    } else {
                        PerfTest.endTest(this)
                        sendMessage(COMMAND_ESS_RTP_PERF_END)
                    }
                }
            }
        }
        "example_menu" {
            permission("essentials.cmd")
            handler {
                checkPlayer(sender.sender) {
                    Gui.startInventory(this) {
                        Navigator(ExampleScreen1())
                    }
                }
            }
        }
    }
}