package ink.pmc.framework.options

import ink.pmc.framework.options.proto.OptionsRpc
import java.util.*

class ProxyOptionsUpdateNotifier : OptionsUpdateNotifier {
    override fun notify(player: UUID) {
        OptionsRpc.notifyBackendContainerUpdate(player)
    }
}