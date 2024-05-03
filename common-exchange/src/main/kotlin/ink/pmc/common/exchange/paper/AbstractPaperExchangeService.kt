package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.AbstractExchangeService
import ink.pmc.common.exchange.IPaperExchangeService
import java.util.*

abstract class AbstractPaperExchangeService : IPaperExchangeService, AbstractExchangeService() {

    abstract val inExchange: List<UUID>
    abstract val statusSnapshots: Map<UUID, StatusSnapshot>

}