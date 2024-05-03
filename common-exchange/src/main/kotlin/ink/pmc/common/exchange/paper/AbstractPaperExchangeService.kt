package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.IPaperExchangeService
import ink.pmc.common.exchange.SharedExchangeService
import java.util.*

abstract class AbstractPaperExchangeService : IPaperExchangeService, SharedExchangeService() {

    abstract val inExchange: List<UUID>

}