package ink.pmc.transfer.api

import ink.pmc.utils.multiplaform.player.PlayerWrapper

@Suppress("UNUSED")
object TransferService : ITransferService by ITransferService.instance

@Suppress("UNUSED")
interface ITransferService {

    companion object {
        lateinit var instance: ITransferService
    }

    val conditionManager: ConditionManager
    val playerCount: Int
    val destinations: Set<Destination>
    val categories: Set<Category>

    fun getDestination(id: String): Destination?

    fun hasDestination(id: String): Boolean

    fun getCategory(id: String): Category?

    fun hasCategory(id: String): Boolean

    fun setMaintenance(destination: Destination, enabled: Boolean)

    suspend fun transferPlayer(player: PlayerWrapper<*>, id: String)

}