package ink.pmc.options

import java.util.*

class BackendOptionsUpdateNotifier : OptionsUpdateNotifier {
    override fun notify(player: UUID) {
        sendContainerUpdateNotify(player)
    }
}