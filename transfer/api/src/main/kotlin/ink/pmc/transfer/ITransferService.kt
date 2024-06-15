package ink.pmc.transfer

object TransferService : ITransferService by ITransferService.instance

interface ITransferService {

    companion object {
        lateinit var instance: ITransferService
    }

}