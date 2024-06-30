package ink.pmc.transfer.proxy

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.FileConfig
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.miniMessage
import ink.pmc.rpc.api.IRpcServer
import ink.pmc.transfer.*
import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.proto.TransferRpc
import ink.pmc.utils.concurrent.submitAsyncIO
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import ink.pmc.utils.visual.mochaSubtext0
import ink.pmc.utils.visual.mochaText
import java.io.File
import java.time.Instant

@Suppress("UNCHECKED_CAST")
class ProxyTransferService(
    proxyServer: ProxyServer,
    rpc: IRpcServer,
    config: FileConfig,
    database: MongoDatabase
) : AbstractProxyTransferService() {

    override val protocol: TransferRpc = TransferRpc(proxyServer, this)
    override val conditionManager: ConditionManager = TODO()
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
            val description = (it["description"] as List<String>?
                ?: listOf()).map { component { miniMessage(it) with mochaSubtext0 } }
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
                (id != null && if (category != null) categories.any { c -> c.id == category } else true).also { bool ->
                    if (!bool && id != null) {
                        serverLogger.warning("Destination $id failed to load!")
                    }
                }
            }.map {
                val id = it["id"] as String
                val icon = KeyedMaterial(it["icon"] as String? ?: "minecraft:paper")
                val name = component { miniMessage(it["name"] as String? ?: id) with mochaText }
                val description = (it["description"] as List<String>?
                    ?: listOf()).map { component { miniMessage(it) with mochaSubtext0 } }
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
        val destination = getDestination(id) ?: throw IllegalStateException("Destination $id not existed")

        if (destination.status != DestinationStatus.ONLINE) {
            throw IllegalStateException("Destination $id not online")
        }

        if (!conditionManager.verifyCondition(player, destination)) {
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

    override fun setMaintenance(destination: Destination, enabled: Boolean) {
        destination as AbstractDestination

        if (enabled && destination.status == DestinationStatus.MAINTENANCE) {
            return
        }

        if (enabled) {
            submitAsyncIO { addMaintenanceEntry(destination) }
            destination.status = DestinationStatus.MAINTENANCE
            return
        }

        if (destination.status != DestinationStatus.MAINTENANCE) {
            return
        }

        submitAsyncIO { removeMaintenanceEntry(destination) }
        destination.status = DestinationStatus.OFFLINE
    }

    override fun close() {
        protocol.close()
    }

}