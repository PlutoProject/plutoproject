package ink.pmc.common.exchange.extensions

import ink.pmc.common.exchange.ExchangeService
import ink.pmc.common.exchange.PaperExchangeService
import ink.pmc.common.member.api.paper.member
import org.bukkit.entity.Player

@Suppress("UNUSED")
val Player.exchangeTickets: Long
    get() = ExchangeService.tickets(this.member)

@Suppress("UNUSED")
fun Player.deposit(amount: Long): Boolean = ExchangeService.deposit(this.member, amount)

@Suppress("UNUSED")
fun Player.withdraw(amount: Long): Boolean = ExchangeService.withdraw(this.member, amount)

@Suppress("UNUSED")
fun Player.match(condition: (Long) -> Boolean): Boolean = ExchangeService.match(this.member, condition)

@Suppress("UNUSED")
fun Player.noLessThan(amount: Long): Boolean = ExchangeService.noLessThan(this.member, amount)

@Suppress("UNUSED")
fun Player.noMoreThan(amount: Long) = ExchangeService.noMoreThan(this.member, amount)

@Suppress("UNUSED")
fun Player.startExchange() = PaperExchangeService.startExchange(this)

@Suppress("UNUSED")
fun Player.endExchange() = PaperExchangeService.endExchange(this)

@Suppress("UNUSED")
suspend fun Player.startExchangeSuspending() = PaperExchangeService.startExchangeSuspending(this)

@Suppress("UNUSED")
suspend fun Player.endExchangeSuspending() = PaperExchangeService.endExchangeSuspending(this)