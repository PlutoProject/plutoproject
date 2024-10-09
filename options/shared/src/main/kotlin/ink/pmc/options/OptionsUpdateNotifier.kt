package ink.pmc.options

import java.util.*

interface OptionsUpdateNotifier {
    fun notify(player: UUID)
}