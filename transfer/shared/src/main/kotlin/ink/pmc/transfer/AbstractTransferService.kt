package ink.pmc.transfer

import ink.pmc.transfer.api.ITransferService
import java.io.Closeable

abstract class AbstractTransferService : ITransferService, Closeable {

    val categories = mutableSetOf<AbstractCategory>()
    val destinations = mutableSetOf<AbstractDestination>()

}