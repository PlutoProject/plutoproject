package ink.pmc.transfer

import ink.pmc.transfer.api.ITransferService
import java.io.Closeable

abstract class AbstractTransferService : ITransferService, Closeable {

    override val categories = mutableSetOf<AbstractCategory>()
    override val destinations = mutableSetOf<AbstractDestination>()

}