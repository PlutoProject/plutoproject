package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.AbstractExchangeService
import ink.pmc.common.exchange.IPaperExchangeService
import ink.pmc.common.member.api.Member
import org.bukkit.entity.Player

class PaperExchangeService : AbstractExchangeService(), IPaperExchangeService {

    override fun startExchange(player: Player) {
        TODO("Not yet implemented")
    }

    override fun endExchange(player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun startExchangeSuspending(player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun endExchangeSuspending(player: Player) {
        TODO("Not yet implemented")
    }

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