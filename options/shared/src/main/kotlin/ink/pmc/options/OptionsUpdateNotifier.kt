package ink.pmc.options

import java.util.UUID

interface OptionsUpdateNotifier {
    fun notify(player: UUID)
}