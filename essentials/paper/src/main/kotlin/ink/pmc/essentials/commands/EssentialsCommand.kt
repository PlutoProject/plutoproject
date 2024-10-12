package ink.pmc.essentials.commands

import ink.pmc.essentials.*
import ink.pmc.essentials.teleport.random.PerfTest
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("essentials")
@Suppress("UNUSED")
fun BukkitCommandManager.essentials(aliases: Array<String>) {
    this("essentials", *aliases) {
        "rtp" {
            permission("essentials.cmd")
            handler {
                sender.sendMessage(COMMAND_ESS_RTP)
            }
        }

        "rtp_perf_test" {
            permission("essentials.cmd")
            handler {
                checkPlayer(sender) {
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

        "huskhomes_migrate" {
            permission("essentials.cmd")
            "warps" {
                permission("essentials.cmd")
                handler {
                    val sender = sender

                    if (huskHomesHook?.huskHomesApi == null) {
                        sender.sendMessage(COMMAND_ESS_HUSKHOMES_NOT_FOUND)
                        return@handler
                    }

                    HuskHomesMigrator.migrateWarps()
                    sender.sendMessage(COMMAND_ESS_HUSKHOMES_MIGRATE_DONE)
                }
            }
            "homes" {
                permission("essentials.cmd")
                handler {
                    val sender = sender

                    if (huskHomesHook?.huskHomesApi == null) {
                        sender.sendMessage(COMMAND_ESS_HUSKHOMES_NOT_FOUND)
                        return@handler
                    }

                    HuskHomesMigrator.migrateHomes()
                    sender.sendMessage(COMMAND_ESS_HUSKHOMES_MIGRATE_DONE)
                }
            }
        }
    }
}