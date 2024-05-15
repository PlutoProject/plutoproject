package ink.pmc.common.member.data

import ink.pmc.common.member.api.data.DataContainer
import ink.pmc.common.member.storage.DataContainerBean
import ink.pmc.common.member.storage.Storable

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