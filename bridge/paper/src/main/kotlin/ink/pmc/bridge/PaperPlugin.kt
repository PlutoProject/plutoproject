package ink.pmc.bridge

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.utils.inject.startKoinIfNotPresent
import org.koin.dsl.module

private val bukkitModule = module {
}

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override fun onEnable() {
        startKoinIfNotPresent {
            modules(bukkitModule)
        }
    }

}