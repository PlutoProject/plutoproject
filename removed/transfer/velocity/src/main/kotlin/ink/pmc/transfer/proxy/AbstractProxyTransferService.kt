package ink.pmc.transfer.proxy

import ink.pmc.transfer.BaseTransferServiceImpl
import ink.pmc.transfer.proto.TransferRpc

abstract class AbstractProxyTransferService : BaseTransferServiceImpl() {

    abstract val protocol: TransferRpc

}