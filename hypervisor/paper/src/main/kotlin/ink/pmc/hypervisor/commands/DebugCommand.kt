package ink.pmc.hypervisor.commands

import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.hypervisor.DynamicScheduling
import ink.pmc.hypervisor.commandManager
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.platform.paper
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

val debugCommand = commandManager.commandBuilder("vd_debug")
    .suspendingHandler {
        val sender = it.sender.sender as? Player ?: return@suspendingHandler
        sender.send {
            text("Current server view distance: ${paper.viewDistance}")
            newline()
            text("Current view distance: ${sender.viewDistance}")
            newline()
            text("Current send view distance: ${sender.sendViewDistance}")
            newline()
            text("Current state: ${DynamicScheduling.getDynamicViewDistanceLocally(sender)}")
            newline()
            text("Current option: ${DynamicScheduling.getDynamicViewDistance(sender)}")
        }
    }

val enabledCommand = commandManager.commandBuilder("vd_debug")
    .literal("enable")
    .suspendingHandler {
        val sender = it.sender.sender as? Player ?: return@suspendingHandler
        runBlocking {
            DynamicScheduling.setDynamicViewDistance(sender, true)
            sender.send {
                text("Enabled")
            }
        }
    }

val disabledCommand = commandManager.commandBuilder("vd_debug")
    .literal("disable")
    .suspendingHandler {
        val sender = it.sender.sender as? Player ?: return@suspendingHandler
        runBlocking {
            DynamicScheduling.setDynamicViewDistance(sender, false)
            sender.send {
                text("Disabled")
            }
        }
    }
