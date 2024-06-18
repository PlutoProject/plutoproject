package ink.pmc.transfer

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.FileConfig
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.miniMessage
import ink.pmc.rpc.api.IRpcServer
import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.proto.TransferRpc
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.visual.mochaSubtext0
import ink.pmc.utils.visual.mochaText
import java.io.File

class ProxyTransferService(
    proxyServer: ProxyServer,
    rpc: IRpcServer,
    private val config: FileConfig
) : AbstractProxyTransferService() {

    override val protocol: TransferRpc = TransferRpc(proxyServer, this)
    override val conditionManager: ConditionManager = ConditionManagerImpl(this)
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

    override fun close() {
        protocol.close()
    }

}