package ink.pmc.transfer

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.FileConfig
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.miniMessage
import ink.pmc.rpc.api.IRpcServer
import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.proto.TransferRpc
import ink.pmc.utils.concurrent.submitAsyncIO
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import ink.pmc.utils.multiplaform.player.velocity.velocity
import ink.pmc.utils.visual.mochaSubtext0
import ink.pmc.utils.visual.mochaText
import java.io.File
import java.time.Instant

class ProxyTransferService(
    proxyServer: ProxyServer,
    rpc: IRpcServer,
    config: FileConfig,
    database: MongoDatabase
) : AbstractProxyTransferService() {

    override val protocol: TransferRpc = TransferRpc(proxyServer, this)
    override val conditionManager: ConditionManager = ConditionManagerImpl(this)
    private val dataCollection = database.getCollection<MaintenanceEntry>("transfer_maintenance_data")
    private val proxySettings = config.get<Config>("proxy-settings")
    private val proxyScriptFile = File(dataDir, proxySettings.get("proxy-script"))
    private val configDestinations = proxySettings.get<List<Map<String, Any>>>("proxy-settings.destinations")
    private val configCategories = proxySettings.get<List<Map<String, Any>>>("proxy-settings.categories")

    init {
        rpc.apply { addService(protocol) }
        loadCategories()
        loadDestinations()
    }

    private fun loadCategories() {
        categories.addAll(configCategories.filter { it["id"] != null }.map {
            val id = it["id"] as String
            val icon = KeyedMaterial(it["icon"] as String? ?: "minecraft:barrel")
            val name = component { miniMessage(it["name"] as String? ?: id) with mochaText }
            val description =
                component { miniMessage(it["description"] as String? ?: "Category $id") with mochaSubtext0 }
            CategoryImpl(
                id,
                0,
                icon,
                name,
                description
            )
        })
    }

    private fun loadDestinations() {
        destinations.addAll(
            configDestinations.filter {
                val id = it["id"] as String?
                val category = it["category"] as String?
                id != null && if (category != null) categories.any { c -> c.id == category } else true
            }.map {
                val id = it["id"] as String
                val icon = KeyedMaterial(it["icon"] as String? ?: "minecraft:paper")
                val name = component { miniMessage(it["name"] as String? ?: id) with mochaText }
                val description =
                    component { miniMessage(it["description"] as String? ?: "Server $id") with mochaSubtext0 }
                val category = categories.firstOrNull { c -> c.id == it["category"] as String? }
                val isHidden = it["isHidden"] as Boolean? ?: false
                val destination = DestinationImpl(
                    id,
                    icon,
                    name,
                    description,
                    category,
                    DestinationStatus.OFFLINE,
                    0,
                    0,
                    isHidden,
                )

                category?.destinations?.add(destination)
                destination
            }
        )
    }

    override suspend fun transferPlayer(player: PlayerWrapper<*>, id: String) {
        val destination = getDestination(id) ?: throw IllegalStateException("Destination named $id not existed")

        if (destination.status != DestinationStatus.ONLINE) {
            throw IllegalStateException("Destination named $id not online")
        }

        if (!conditionManager.verifyCondition(player.velocity, destination)) {
            return
        }

        player.switchServer(id)
    }

    private suspend fun addMaintenanceEntry(destination: Destination) {
        dataCollection.insertOne(MaintenanceEntry(destination.id, Instant.now().toEpochMilli()))
    }

    private suspend fun removeMaintenanceEntry(destination: Destination) {
        dataCollection.deleteOne(eq("id", destination.id))
    }

    override fun setMaintainace(destination: Destination, enabled: Boolean) {
        destination as AbstractDestination
        destination.status = when (enabled) {
            true -> {
                if (destination.status == DestinationStatus.MAINTENANCE) {
                    return
                }

                submitAsyncIO { addMaintenanceEntry(destination) }
                DestinationStatus.MAINTENANCE
            }

            false -> {
                if (destination.status != DestinationStatus.MAINTENANCE) {
                    return
                }

                submitAsyncIO { removeMaintenanceEntry(destination) }
                DestinationStatus.OFFLINE
            }
        }
    }

    override fun close() {
        protocol.close()
    }

}