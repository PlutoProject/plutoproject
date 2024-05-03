package ink.pmc.common.exchange

import ink.pmc.common.member.api.Member

abstract class SharedExchangeService : AbstractExchangeService() {

    override fun tickets(member: Member): Long {
        return member.dataContainer.getLong(EXCHANGE_TICKET_KEY) ?: run { println("null"); 0 }
    }

    override fun tickets(member: Member, amount: Long): Boolean {
        return try {
            member.dataContainer[EXCHANGE_TICKET_KEY] = amount
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun deposit(member: Member, amount: Long): Boolean {
        return try {
            member.dataContainer[EXCHANGE_TICKET_KEY] = tickets(member) + amount
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
            member.dataContainer[EXCHANGE_TICKET_KEY] = tickets(member) - amount
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun match(member: Member, condition: (Long) -> Boolean): Boolean {
        return condition.invoke(tickets(member))
    }

}