package ink.pmc.framework.options

import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.options.proto.OptionsRpcGrpcKt
import ink.pmc.framework.options.proto.optionsUpdateNotify
import ink.pmc.options.api.OptionsManager
import ink.pmc.rpc.api.RpcClient
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.player.isBukkitOnline
import ink.pmc.utils.player.uuid
import ink.pmc.utils.proto.empty
import io.grpc.StatusException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.*
import java.util.logging.Level
import kotlin.time.Duration.Companion.seconds

private lateinit var monitorJob: Job
private val id: UUID = UUID.randomUUID()
private val stub = OptionsRpcGrpcKt.OptionsRpcCoroutineStub(RpcClient.channel)

fun sendContainerUpdateNotify(player: UUID) {
    submitAsync {
        stub.notifyOptionsUpdate(optionsUpdateNotify {
            serverId = id.toString()
            this.player = player.toString()
        })
    }
}

fun startOptionsMonitor() {
    monitorJob = submitAsync {
        while (true) {
            try {
                stub.monitorOptionsUpdate(empty).also {
                    frameworkLogger.info("Monitor stream connected")
                }.collect {
                    val serverId = it.serverId.uuid
                    val player = it.player.uuid
                    if (!player.isBukkitOnline || serverId == id) {
                        return@collect
                    }
                    OptionsManager.reloadOptions(player)
                }
            } catch (e: StatusException) {
                frameworkLogger.log(Level.SEVERE, "Monitor stream interrupted, reconnect after 10 seconds", e)
                delay(10.seconds)
            }
        }
    }
}

fun stopOptionsMonitor() {
    monitorJob.cancel()
}