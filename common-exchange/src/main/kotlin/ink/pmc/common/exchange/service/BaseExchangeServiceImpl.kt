package ink.pmc.common.exchange.service

import ink.pmc.common.exchange.TICKET_KEY
import ink.pmc.common.member.api.Member

abstract class BaseExchangeServiceImpl<T> : AbstractExchangeService<T>() {

    override fun tickets(member: Member): Long {
        return member.dataContainer.getLong(TICKET_KEY) ?: 0
    }

    override fun tickets(member: Member, amount: Long): Boolean {
        return try {
            member.dataContainer[TICKET_KEY] = amount
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun deposit(member: Member, amount: Long): Boolean {
        return try {
            member.dataContainer[TICKET_KEY] = tickets(member) + amount
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun withdraw(member: Member, amount: Long): Boolean {
        return try {
            if (!noLessThan(member, amount)) {
                return false
            }
            member.dataContainer[TICKET_KEY] = tickets(member) - amount
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun match(member: Member, condition: (Long) -> Boolean): Boolean {
        return condition.invoke(tickets(member))
    }

}