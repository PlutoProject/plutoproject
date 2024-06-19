package ink.pmc.transfer

import ink.pmc.transfer.proto.TransferRpc

abstract class AbstractProxyTransferService : BaseTransferServiceImpl() {

    abstract val protocol: TransferRpc

}