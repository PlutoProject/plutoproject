package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.DestinationStatus

abstract class AbstractDestination : Destination {

    abstract override var category: Category
    abstract override var status: DestinationStatus
    abstract override var playerCount: Int

}