package ink.pmc.common.exchange.backend

import com.google.protobuf.Empty
import ink.pmc.common.exchange.proto.ExchangeStatusOuterClass.ExchangeStatus
import ink.pmc.common.exchange.proto.lobby2proxy.ItemDistributeNotifyOuterClass.ItemDistributeNotify
import ink.pmc.common.exchange.proto.server2lobby.exchangeEnd
import ink.pmc.common.exchange.proto.server2lobby.exchangeStart
import ink.pmc.common.exchange.serverLogger
import ink.pmc.common.exchange.serverName
import ink.pmc.common.exchange.utils.decodeItems
import ink.pmc.common.exchange.utils.distributeItems
import ink.pmc.common.utils.concurrent.submitAsyncIO
import ink.pmc.common.utils.proto.player.player
import kotlinx.coroutines.Job
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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
        itemDistributeNotifyHandler.cancel()
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

    private fun handleItemDistribute(notify: ItemDistributeNotify) {
        val player = Bukkit.getPlayer(notify.player.uuid) ?: return
        player.distributeItems(notify.itemList.decodeItems())
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