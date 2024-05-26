package ink.pmc.exchange.api

import ink.pmc.member.api.Member

@Suppress("UNUSED")
interface IExchangeService<T> {

    companion object {
        lateinit var instance: IExchangeService<*>
    }

    suspend fun startExchange(player: T)

    suspend fun endExchange(player: T)

    suspend fun isInExchange(player: T): Boolean

    fun tickets(member: Member): Long

    fun tickets(member: Member, amount: Long): Boolean

    fun deposit(member: Member, amount: Long): Boolean

    fun withdraw(member: Member, amount: Long): Boolean

    fun match(member: Member, condition: (Long) -> Boolean): Boolean

    fun noLessThan(member: Member, amount: Long): Boolean

    fun noMoreThan(member: Member, amount: Long): Boolean

}