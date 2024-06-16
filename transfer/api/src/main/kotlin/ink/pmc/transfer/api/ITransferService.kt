package ink.pmc.transfer.api

import ink.pmc.utils.multiplaform.player.PlayerWrapper

@Suppress("UNUSED")
object TransferService : ITransferService by ITransferService.instance

@Suppress("UNUSED")
interface ITransferService {

    companion object {
        lateinit var instance: ITransferService
    }

    val playerCount: Int

    fun getDestination(id: String): Destination?

    fun hasDestination(id: String): Boolean

    fun getCategory(id: String): Category?

    fun hasCategory(id: String): Boolean

    suspend fun transferPlayer(player: PlayerWrapper<*>, id: String)

}