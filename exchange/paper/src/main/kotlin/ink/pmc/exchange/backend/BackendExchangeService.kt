package ink.pmc.exchange.backend

import com.google.protobuf.Empty
import ink.pmc.exchange.proto.ExchangeStatusOuterClass.ExchangeStatus
import ink.pmc.exchange.proto.lobby2proxy.ItemDistributeNotifyOuterClass.ItemDistributeNotify
import ink.pmc.exchange.proto.server2lobby.exchangeEnd
import ink.pmc.exchange.proto.server2lobby.exchangeStart
import ink.pmc.exchange.serverLogger
import ink.pmc.exchange.serverName
import ink.pmc.exchange.utils.distributeItems
import ink.pmc.utils.concurrent.submitAsyncIO
import ink.pmc.utils.player.itemStackArrayFromBase64
import ink.pmc.utils.proto.player.player
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.logging.Level

class BackendExchangeService : AbstractBackendExchangeService() {

    private lateinit var itemDistributeNotifyHandler: Job

    init {
        startBackGroundJobs()
    }

    override fun startBackGroundJobs() {
        itemDistributeNotifyHandler = monitorItemDistribute()
    }

    override suspend fun stopBackGroundJobs() {
        itemDistributeNotifyHandler.cancelAndJoin()
    }

    private fun monitorItemDistribute(): Job {
        return submitAsyncIO {
            try {
                stub.monitorItemDistribute(Empty.getDefaultInstance()).collect {
                    handleItemDistribute(it)
                }
            } catch (e: Exception) {
                serverLogger.log(Level.SEVERE, "Exception caught while monitoring item distribute", e)
            }
        }
    }

    private suspend fun handleItemDistribute(notify: ItemDistributeNotify) {
        val player = Bukkit.getPlayer(UUID.fromString(notify.player.uuid)) ?: return
        player.distributeItems(itemStackArrayFromBase64(notify.items).toList().filterNotNull())
    }

    override suspend fun startExchange(player: Player) {
        stub.startExchange(exchangeStart {
            serviceId = id.toString()
            server = serverName
            this.player = player {
                username = player.name
                uuid = player.uniqueId.toString()
            }
        })
    }

    override suspend fun endExchange(player: Player) {
        stub.endExchange(exchangeEnd {
            serviceId = id.toString()
            this.player = player {
                username = player.name
                uuid = player.uniqueId.toString()
            }
        })
    }

    override suspend fun isInExchange(player: Player): Boolean {
        val ack = stub.isInExchange(player {
            username = player.name
            uuid = player.uniqueId.toString()
        })

        return ack.status == ExchangeStatus.IN_EXCHANGE
    }
}