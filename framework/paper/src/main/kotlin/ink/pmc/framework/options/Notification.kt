package ink.pmc.framework.options

import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.options.proto.OptionsRpcGrpcKt
import ink.pmc.framework.options.proto.optionsUpdateNotify
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.player.isBukkitOnline
import ink.pmc.framework.utils.player.uuid
import ink.pmc.framework.utils.proto.empty
import ink.pmc.options.api.OptionsManager
import ink.pmc.rpc.api.RpcClient
import io.grpc.StatusException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.*
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
                    frameworkLogger.info("Options monitor stream connected")
                }.collect {
                    val serverId = it.serverId.uuid
                    val player = it.player.uuid
                    if (!player.isBukkitOnline || serverId == id) {
                        return@collect
                    }
                    OptionsManager.reloadOptions(player)
                }
            } catch (e: StatusException) {
                delay(10.seconds)
            }
        }
    }
}

fun stopOptionsMonitor() {
    monitorJob.cancel()
}