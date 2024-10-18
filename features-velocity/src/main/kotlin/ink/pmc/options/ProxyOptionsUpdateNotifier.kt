package ink.pmc.options

import ink.pmc.options.proto.OptionsRpc
import java.util.*

class ProxyOptionsUpdateNotifier : OptionsUpdateNotifier {
    override fun notify(player: UUID) {
        OptionsRpc.notifyBackendContainerUpdate(player)
    }
}