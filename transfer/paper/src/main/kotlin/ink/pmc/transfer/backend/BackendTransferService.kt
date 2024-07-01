package ink.pmc.transfer.backend

import com.electronwill.nightconfig.core.Config
import com.google.protobuf.Empty
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.miniMessage
import ink.pmc.transfer.*
import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.proto.CategoryBundleOuterClass.CategoryBundle
import ink.pmc.transfer.proto.DestinationBundleOuterClass.DestinationBundle
import ink.pmc.transfer.proto.TransferRpcGrpcKt.TransferRpcCoroutineStub
import ink.pmc.transfer.proto.TransferRspOuterClass.TransferResult
import ink.pmc.transfer.proto.healthyReport
import ink.pmc.transfer.proto.transferReq
import ink.pmc.utils.concurrent.submitAsyncIO
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import kotlinx.coroutines.delay
import org.bukkit.Server
import kotlin.time.Duration.Companion.seconds

class BackendTransferService(
    private val server: Server,
    private val stub: TransferRpcCoroutineStub,
    config: Config
) : BaseTransferServiceImpl() {

    private var closed = false
    override val conditionManager: ConditionManager = BackendConditionManager(stub)
    private val id = config.get<String>("id")
    private val summaryRefresh = submitAsyncIO {
        while (!closed) {
            delay(5.seconds)
            val summary = stub.getSummary(Empty.getDefaultInstance())
            refreshCategories(summary.categories)
            refreshDestinations(summary.destinations)
        }
    }
    private val healthyReport = submitAsyncIO {
        while (!closed) {
            delay(5.seconds)
            stub.reportHealthy(healthyReport {
                id = this@BackendTransferService.id
                playerCount = server.onlinePlayers.size
                maxPlayerCount = server.maxPlayers
            })
        }
    }

    private fun refreshCategories(bundle: CategoryBundle) {
        bundle.categoriesList.forEach {
            val id = it.id
            val playerCount = it.playerCount
            val icon = KeyedMaterial(it.icon)
            val name = component { miniMessage(it.name) }
            val description = it.descriptionList.map { component { miniMessage(it) } }

            getCategory(id)?.let { c ->
                c as AbstractCategory
                c.playerCount = playerCount
                return@forEach
            }

            categories.add(
                CategoryImpl(
                    it.id,
                    it.playerCount,
                    icon,
                    name,
                    description
                )
            )
        }
    }

    private fun refreshDestinations(bundle: DestinationBundle) {
        bundle.destinationsList.forEach {
            val id = it.id
            val icon = KeyedMaterial(it.icon)
            val name = component { miniMessage(it.name) }
            val description = it.descriptionList.map { component { miniMessage(it) } }
            val category = getCategory(it.category)
            val status = it.status.original
            val playerCount = it.playerCount
            val maxPlayerCount = it.maxPlayerCount
            val isHidden = it.isHidden

            getDestination(id)?.let { d ->
                d as AbstractDestination
                d.status = status
                d.playerCount = playerCount
                d.maxPlayerCount = maxPlayerCount
                return@forEach
            }

            if (category == null && it.category != null) {
                serverLogger.warning("Failed to load destination $id because category ${it.category} not found!")
                return@forEach
            }

            destinations.add(
                DestinationImpl(
                    id,
                    icon,
                    name,
                    description,
                    category,
                    status,
                    playerCount,
                    maxPlayerCount,
                    isHidden
                )
            )
        }
    }

    override fun setMaintenance(destination: Destination, enabled: Boolean) {
        throw UnsupportedOperationException("Maintenance can only be toggled on proxy")
    }

    override suspend fun transferPlayer(player: PlayerWrapper<*>, id: String) {
        val result = stub.transferPlayer(transferReq {
            uuid = player.uuid.toString()
            destination = id
        })

        when(result.result) {
            TransferResult.TRANSFER_FAILED_DEST_NOT_EXISTED -> throw IllegalStateException("Destination named $id not existed")
            TransferResult.TRANSFER_FAILED_DEST_OFFLINE -> throw IllegalStateException("Destination named $id not existed")
            TransferResult.TRANSFER_FAILED_PLAYER_OFFLINE -> throw IllegalStateException("Player ${player.name} offline")
            else -> {}
        }
    }

    override fun close() {
        closed = true
        summaryRefresh.cancel()
        healthyReport.cancel()
    }

}