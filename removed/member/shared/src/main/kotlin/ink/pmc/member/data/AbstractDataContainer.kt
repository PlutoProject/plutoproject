package ink.pmc.member.data

import ink.pmc.member.api.data.DataContainer
import ink.pmc.member.storage.DataContainerBean
import ink.pmc.member.storage.Storable

abstract class AbstractDataContainer : DataContainer, Storable<DataContainerBean> {

    abstract var bean: DataContainerBean

    override fun equals(other: Any?): Boolean {
        if (other !is DataContainer) {
            return false
        }

        return other.id == id
    }

    override fun hashCode(): Int {
        return bean.hashCode()
    }

}