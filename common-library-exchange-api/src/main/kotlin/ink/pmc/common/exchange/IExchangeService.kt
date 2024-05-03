package ink.pmc.common.exchange

import ink.pmc.common.member.api.Member

@Suppress("UNUSED")
interface IExchangeService {

    companion object {
        lateinit var instance: IExchangeService
    }

    fun tickets(member: Member): Long

    fun tickets(member: Member, amount: Long): Boolean

    fun deposit(member: Member, amount: Long): Boolean

    fun withdraw(member: Member, amount: Long): Boolean

    fun match(member: Member, condition: (Long) -> Boolean): Boolean

    fun noLessThan(member: Member, amount: Long): Boolean

    fun noMoreThan(member: Member, amount: Long): Boolean

}