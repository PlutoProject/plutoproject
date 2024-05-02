package ink.pmc.common.exchange.extensions

import ink.pmc.common.exchange.ExchangeService
import ink.pmc.common.member.api.Member

@Suppress("UNUSED")
val Member.exchangeTickets: Long
    get() = ExchangeService.tickets(this)

@Suppress("UNUSED")
fun Member.deposit(amount: Long): Boolean = ExchangeService.deposit(this, amount)

@Suppress("UNUSED")
fun Member.withdraw(amount: Long): Boolean = ExchangeService.withdraw(this, amount)

@Suppress("UNUSED")
fun Member.match(condition: (Long) -> Boolean): Boolean = ExchangeService.match(this, condition)

@Suppress("UNUSED")
fun Member.noLessThan(amount: Long): Boolean = ExchangeService.noLessThan(this, amount)

@Suppress("UNUSED")
fun Member.noMoreThan(amount: Long) = ExchangeService.noMoreThan(this, amount)