package ink.pmc.bridge

import ink.pmc.bridge.api.GeyserBridge
import ink.pmc.bridge.proto.GeyserBridgeRpcGrpcKt.GeyserBridgeRpcCoroutineStub
import ink.pmc.bridge.proto.playerIdentifier
import ink.pmc.rpc.api.RpcClient
import ink.pmc.utils.concurrent.submitAsync
import org.bukkit.entity.Player

class GeyserBridgeImpl : GeyserBridge {

    private val stub = GeyserBridgeRpcCoroutineStub(RpcClient.channel)

    override suspend fun closeForm(player: Player) {
        stub.closeForm(playerIdentifier { uuid = player.uniqueId.toString() })
    }

    override fun closeFormAsync(player: Player) {
        submitAsync {
            closeForm(player)
        }
    }

}