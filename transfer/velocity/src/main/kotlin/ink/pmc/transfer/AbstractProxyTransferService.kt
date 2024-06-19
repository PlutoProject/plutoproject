package ink.pmc.transfer

import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.proto.TransferRpc

abstract class AbstractProxyTransferService : BaseTransferServiceImpl() {

    abstract val protocol: TransferRpc
    abstract val conditionManager: ConditionManager
    abstract val globalMaintenance: Boolean

    abstract fun setGlobalMaintenance(enabled: Boolean)

}