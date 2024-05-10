package ink.pmc.common.rpc.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class RpcClientDisconnectEvent : Event() {

    companion object {
        private val handlers = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return Companion.handlers
    }

}