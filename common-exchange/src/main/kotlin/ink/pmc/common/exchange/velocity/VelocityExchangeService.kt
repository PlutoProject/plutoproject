package ink.pmc.common.exchange.velocity

import ink.pmc.common.exchange.AbstractExchangeService
import ink.pmc.common.member.api.Member

class VelocityExchangeService : AbstractExchangeService() {

    override fun tickets(member: Member): Long {
        TODO("Not yet implemented")
    }

    override fun deposit(member: Member, amount: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun withdraw(member: Member, amount: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun match(member: Member, condition: (Long) -> Boolean): Boolean {
        TODO("Not yet implemented")
    }

}