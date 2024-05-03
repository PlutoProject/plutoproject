package ink.pmc.common.exchange.extensions

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.exchange.ExchangeService
import ink.pmc.common.member.api.velocity.member

@Suppress("UNUSED")
val Player.exchangeTickets: Long
    get() = ExchangeService.tickets(this.member)

@Suppress("UNUSED")
fun Player.tickets(amount: Long): Boolean = ExchangeService.tickets(this.member, amount)

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