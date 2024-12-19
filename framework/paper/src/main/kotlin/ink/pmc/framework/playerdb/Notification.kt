package ink.pmc.framework.playerdb

import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.playerdb.proto.PlayerDbRpcGrpcKt
import ink.pmc.framework.playerdb.proto.databaseIdentifier
import ink.pmc.framework.rpc.RpcClient
import ink.pmc.framework.concurrent.submitAsync
import ink.pmc.framework.concurrent.submitAsyncIO
import ink.pmc.framework.player.db.PlayerDb
import ink.pmc.framework.player.uuid
import ink.pmc.framework.proto.empty
import io.grpc.StatusException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.*
import kotlin.time.Duration.Companion.seconds

private lateinit var monitorJob: Job
private val identity = UUID.randomUUID()
private val stub = PlayerDbRpcGrpcKt.PlayerDbRpcCoroutineStub(RpcClient.channel)

fun sendUpdateNotification(id: UUID) {
    submitAsyncIO {
        stub.notify(databaseIdentifier {
            serverId = identity.toString()
            uuid = id.toString()
        })
    }
}

fun startPlayerDbMonitor() {
    monitorJob = submitAsync {
        while (true) {
            try {
                stub.monitorNotify(empty).also {
                    frameworkLogger.info("Try to connect player-database monitor stream")
                }.collect {
                    if (it.serverId.uuid == identity) return@collect
                    val id = it.uuid.uuid
                    if (!PlayerDb.isLoaded(id)) return@collect
                    PlayerDb.reload(id)
                }
            } catch (e: StatusException) {
                delay(10.seconds)
            }
        }
    }
}

fun stopPlayerDbMonitor() {
    monitorJob.cancel()
}