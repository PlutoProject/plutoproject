package ink.pmc.transfer

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.FileConfig
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.proto.ConditionVerify.ConditionVerifyResult
import ink.pmc.transfer.proto.TransferRpcGrpcKt.TransferRpcCoroutineStub
import ink.pmc.transfer.proto.conditionVerifyReq
import ink.pmc.transfer.proto.healthyReport
import ink.pmc.utils.concurrent.submitAsyncIO
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
    private val summaryRefresh = submitAsyncIO { }
    private val healthyReport = submitAsyncIO {
        while (!closed) {
            delay(5.seconds)
            stub.reportHealthy(healthyReport {
                id = this@BackendTransferService.id
                playerCount = server.onlinePlayers.size
            })
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