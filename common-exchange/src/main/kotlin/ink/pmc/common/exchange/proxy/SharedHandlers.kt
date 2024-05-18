package ink.pmc.common.exchange.proxy

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import ink.pmc.common.exchange.*
import ink.pmc.common.exchange.utils.isInExchangeLobby
import ink.pmc.common.member.api.velocity.member
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.visual.mochaText
import ink.pmc.common.utils.visual.mochaYellow
import net.kyori.adventure.text.Component

suspend fun exchange(player: Player) {
    if (!proxyExchangeService.isLobbyHealthy()) {
        player.sendMessage(EXCHANGE_LOBBY_OFFLINE)
        return
    }

    if (proxyExchangeService.isInExchange(player) || player.isInExchangeLobby) {
        player.sendMessage(EXCHANGE_START_FAILED_ALREADY_IN)
        return
    }

    proxyExchangeService.startExchange(player)
    player.sendMessage(EXCHANGE_START_SUCCEED)
}

suspend fun back(player: Player) {
    if (!proxyExchangeService.isLobbyHealthy()) {
        player.sendMessage(EXCHANGE_LOBBY_OFFLINE)
        return
    }

    if (!proxyExchangeService.isInExchange(player) || !player.isInExchangeLobby) {
        player.sendMessage(EXCHANGE_END_FAILED_NOT_IN)
        return
    }

    proxyExchangeService.endExchange(player)
    player.sendMessage(EXCHANGE_END_SUCCEED)
}

suspend fun tickets(player: Player, receiver: CommandSource) {
    val member = player.member()
    val amount = exchangeService.tickets(member)

    when (receiver is Player && player == receiver) {
        true -> {
            receiver.sendMessage(TICKETS_LOOKUP_SELF.replace("<amount>", Component.text(amount).color(mochaText)))
        }

        false -> {
            receiver.sendMessage(
                TICKETS_LOOKUP_OTHER
                    .replace("<amount>", Component.text(amount).color(mochaText))
                    .replace("<player>", Component.text(player.username).color(mochaYellow))
            )
        }
    }
}