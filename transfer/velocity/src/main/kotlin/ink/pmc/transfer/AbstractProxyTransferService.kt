package ink.pmc.transfer

import ink.pmc.transfer.api.ConditionManager

abstract class AbstractProxyTransferService : AbstractTransferService() {

    abstract val conditionManager: ConditionManager

}