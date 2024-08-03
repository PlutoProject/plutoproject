package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.Destination

abstract class AbstractCategory : Category {

    abstract override var playerCount: Int
    override val destinations: MutableSet<Destination> = mutableSetOf()

}