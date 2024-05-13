package ink.pmc.common.member.data

import ink.pmc.common.member.api.data.DataContainer
import ink.pmc.common.member.storage.DataContainerStorage

abstract class AbstractDataContainer : DataContainer {

    abstract var storage: DataContainerStorage

    override fun equals(other: Any?): Boolean {
        if (other !is DataContainer) {
            return false
        }

        return other.id == id
    }

    override fun hashCode(): Int {
        return storage.hashCode()
    }

    abstract fun reload(storage: DataContainerStorage)

}