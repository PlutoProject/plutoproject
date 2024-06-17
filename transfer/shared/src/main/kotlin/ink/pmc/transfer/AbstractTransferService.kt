package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.ITransferService

abstract class AbstractTransferService : ITransferService {

    val categories = mutableSetOf<Category>()
    val destinations = mutableSetOf<AbstractDestination>()

}