package ink.pmc.transfer

import ink.pmc.transfer.api.ITransferService

abstract class AbstractTransferService : ITransferService {

    val categories = mutableSetOf<AbstractCategory>()
    val destinations = mutableSetOf<AbstractDestination>()

}