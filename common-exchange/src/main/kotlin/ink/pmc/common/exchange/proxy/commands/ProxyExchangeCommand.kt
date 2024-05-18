package ink.pmc.common.exchange.proxy.commands

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.exchange.*
import ink.pmc.common.exchange.proxy.back
import ink.pmc.common.exchange.proxy.exchange
import ink.pmc.common.exchange.utils.isInExchangeLobby
import ink.pmc.common.utils.chat.NON_PLAYER
import ink.pmc.common.utils.chat.PLAYER_NOT_ONLINE
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.command.VelocityCommand
import ink.pmc.common.utils.command.velocityRequiredOnlinePlayersArgument
import ink.pmc.common.utils.platform.proxy
import ink.pmc.common.utils.visual.mochaText
import ink.pmc.common.utils.visual.mochaYellow
import net.kyori.adventure.text.Component
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.kotlin.extension.commandBuilder

object ProxyExchangeCommand : VelocityCommand() {

    private val exchange = velocityCommandManager.commandBuilder("exchange") {
        suspendingHandler {
            val sender = it.sender()

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            exchange(sender)
        }
    }.commandBuilder

    private val exchangeBack = velocityCommandManager.commandBuilder("back", aliases = arrayOf("quit")) {
        suspendingHandler {
            val sender = it.sender()

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            back(sender)
        }
    }.commandBuilder

    private val exchangeAdminStart = velocityCommandManager.commandBuilder("exchange") {
        permission(EXCHANGE_ADMIN_PERMISSION)
        literal("admin")
        literal("start")
        argument(velocityRequiredOnlinePlayersArgument())

        suspendingHandler {
            val sender = it.sender()
            val target = it.get<String>("name")

            if (proxy.getPlayer(target).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val player = proxy.getPlayer(target).get()

            if (proxyExchangeService.isInExchange(player) || player.isInExchangeLobby) {
                sender.sendMessage(
                    EXCHANGE_ADMIN_START_FAILED_ALREADY_IN.replace(
                        "<player>", Component.text(target).color(
                            mochaYellow
                        )
                    )
                )
                return@suspendingHandler
            }

            sender.sendMessage(
                EXCHANGE_ADMIN_START_SUCCEED.replace(
                    "<player>", Component.text(target).color(
                        mochaText
                    )
                )
            )
            exchange(player)
        }
    }.commandBuilder

    private val exchangeAdminEnd = velocityCommandManager.commandBuilder("exchange") {
        permission(EXCHANGE_ADMIN_PERMISSION)
        literal("admin")
        literal("end")
        argument(velocityRequiredOnlinePlayersArgument())

        suspendingHandler {
            val sender = it.sender()
            val target = it.get<String>("name")

            if (proxy.getPlayer(target).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val player = proxy.getPlayer(target).get()

            if (!proxyExchangeService.isInExchange(player) || !player.isInExchangeLobby) {
                sender.sendMessage(
                    EXCHANGE_ADMIN_END_FAILED_NOT_IN.replace(
                        "<player>", Component.text(target).color(
                            mochaYellow
                        )
                    )
                )
                return@suspendingHandler
            }

            sender.sendMessage(
                EXCHANGE_ADMIN_END_SUCCEED.replace(
                    "<player>", Component.text(target).color(
                        mochaText
                    )
                )
            )
            back(player)
        }
    }.commandBuilder

    init {
        command(exchange)
        command(exchangeBack)
        command(exchangeAdminStart)
        command(exchangeAdminEnd)
    }

}