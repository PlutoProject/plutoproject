package ink.pmc.interactive.api.gui.form

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.geysermc.cumulus.form.Form
import org.geysermc.cumulus.response.FormResponse
import org.geysermc.cumulus.response.result.FormResponseResult

@Suppress("UNUSED")
class InventoryFormResultEvent(
    private val form: Form,
    private val result: FormResponseResult<out FormResponse>,
    who: Player
) : PlayerEvent(who) {

    companion object {
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlers
    }

    override fun getHandlers(): HandlerList {
        return Companion.handlers
    }

}