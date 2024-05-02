package ink.pmc.common.exchange

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin

var disable = true

@Suppress("UNUSED")
class ExchangePlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        disable = false
    }

    override suspend fun onDisableAsync() {
        disable = true
    }

}