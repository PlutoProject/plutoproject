package ink.pmc.essentials.commands

import ink.pmc.essentials.COMMAND_ESS_RTP
import ink.pmc.essentials.COMMAND_ESS_RTP_PERF_END
import ink.pmc.essentials.COMMAND_ESS_RTP_PERF_START
import ink.pmc.essentials.Cm
import ink.pmc.essentials.teleport.random.PerfTest
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("essentials")
@Suppress("UNUSED")
fun Cm.essentials(aliases: Array<String>) {
    this("essentials", *aliases) {
        permission("essentials.cmd")
        "rtp" {
            handler {
                sender.sender.sendMessage(COMMAND_ESS_RTP)
            }
        }
        "rtp_perf_test" {
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
    }
}