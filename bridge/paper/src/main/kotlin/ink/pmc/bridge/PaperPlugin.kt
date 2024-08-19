package ink.pmc.bridge

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.bridge.api.GeyserBridge
import ink.pmc.utils.inject.startKoinIfNotPresent
import org.koin.dsl.module

private val bukkitModule = module {
    single<GeyserBridge> { GeyserBridgeImpl() }
}

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override fun onEnable() {
        startKoinIfNotPresent {
            modules(bukkitModule)
        }
    }

}