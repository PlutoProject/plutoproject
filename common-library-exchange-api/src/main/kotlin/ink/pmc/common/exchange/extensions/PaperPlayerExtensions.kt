package ink.pmc.common.exchange.extensions

import ink.pmc.common.exchange.ExchangeService
import ink.pmc.common.exchange.PaperExchangeService
import ink.pmc.common.member.api.paper.member
import org.bukkit.entity.Player

@Suppress("UNUSED")
suspend fun Player.exchangeTickets(): Long = ExchangeService.tickets(this.member())

@Suppress("UNUSED")
suspend fun Player.tickets(amount: Long): Boolean = ExchangeService.tickets(this.member(), amount)

@Suppress("UNUSED")
suspend fun Player.deposit(amount: Long): Boolean = ExchangeService.deposit(this.member(), amount)

@Suppress("UNUSED")
suspend fun Player.withdraw(amount: Long): Boolean = ExchangeService.withdraw(this.member(), amount)

@Suppress("UNUSED")
suspend fun Player.match(condition: (Long) -> Boolean): Boolean = ExchangeService.match(this.member(), condition)

@Suppress("UNUSED")
suspend fun Player.noLessThan(amount: Long): Boolean = ExchangeService.noLessThan(this.member(), amount)

@Suppress("UNUSED")
suspend fun Player.noMoreThan(amount: Long) = ExchangeService.noMoreThan(this.member(), amount)

@Suppress("UNUSED")
suspend fun Player.startExchange() = PaperExchangeService.startExchange(this)

@Suppress("UNUSED")
suspend fun Player.endExchange() = PaperExchangeService.endExchange(this)