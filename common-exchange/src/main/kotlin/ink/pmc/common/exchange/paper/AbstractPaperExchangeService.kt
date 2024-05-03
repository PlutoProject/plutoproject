package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.AbstractExchangeService
import ink.pmc.common.exchange.IPaperExchangeService
import ink.pmc.common.exchange.SharedExchangeService
import java.util.*

abstract class AbstractPaperExchangeService : IPaperExchangeService, SharedExchangeService() {

    abstract val inExchange: List<UUID>
    abstract val statusSnapshots: Map<UUID, StatusSnapshot>

}