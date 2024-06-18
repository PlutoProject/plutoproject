package ink.pmc.transfer

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.FileConfig
import com.google.protobuf.Empty
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.miniMessage
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.proto.CategoryBundleOuterClass.CategoryBundle
import ink.pmc.transfer.proto.ConditionVerify.ConditionVerifyResult
import ink.pmc.transfer.proto.DestinationBundleOuterClass.DestinationBundle
import ink.pmc.transfer.proto.TransferRpcGrpcKt.TransferRpcCoroutineStub
import ink.pmc.transfer.proto.conditionVerifyReq
import ink.pmc.transfer.proto.healthyReport
import ink.pmc.utils.concurrent.submitAsyncIO
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import kotlinx.coroutines.delay
import org.bukkit.Server
import kotlin.time.Duration.Companion.seconds

class BackendTransferService(
    private val server: Server,
    private val stub: TransferRpcCoroutineStub,
    config: FileConfig
) : BaseTransferServiceImpl() {

    private var closed = false
    private val backendSettings = config.get<Config>("backend-settings")
    private val id = backendSettings.get<String>("id")
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
            })
        }
    }

    private fun refreshCategories(bundle: CategoryBundle) {
        bundle.categoriesList.forEach {
            val id = it.id
            val playerCount = it.playerCount
            val icon = KeyedMaterial(it.icon)
            val name = component { miniMessage(it.name) }
            val description = component { miniMessage(it.description) }

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
            val description = component { miniMessage(it.description) }
            val category = getCategory(it.category)
            val status = it.status.original
            val playerCount = it.playerCount
            val maxPlayerCount = it.maxPlayerCount
            val isHidden = it.isHidden

            getDestination(id)?.let { d ->
                d as AbstractDestination
                d.status = status
                d.playerCount = playerCount
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

    override fun setMaintainace(destination: Destination, enabled: Boolean) {
        throw UnsupportedOperationException("Maintainace can only be toggled on proxy")
    }

    override suspend fun transferPlayer(player: PlayerWrapper<*>, id: String) {
        val destination = getDestination(id) ?: throw IllegalStateException("Destination named $id not existed")

        if (destination.status != DestinationStatus.ONLINE) {
            throw IllegalStateException("Destination named $id not online")
        }

        val condition = stub.verifyCondition(conditionVerifyReq {
            uuid = player.uuid.toString()
            this.destination = id
        })

        if (condition.result != ConditionVerifyResult.VERIFY_SUCCEED) {
            return
        }

        player.switchServer(id)
    }

    override fun close() {
        closed = true
        summaryRefresh.cancel()
        healthyReport.cancel()
    }

}