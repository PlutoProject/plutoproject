package ink.pmc.visual

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.visual.api.display.text.TextDisplayFactory
import ink.pmc.visual.api.display.text.TextDisplayManager
import ink.pmc.visual.api.display.text.TextDisplayRenderer
import ink.pmc.visual.api.toast.ToastFactory
import ink.pmc.visual.api.toast.ToastRenderer
import ink.pmc.visual.display.text.TextDisplayFactoryImpl
import ink.pmc.visual.display.text.TextDisplayListener
import ink.pmc.visual.display.text.TextDisplayManagerImpl
import ink.pmc.visual.display.text.renderers.BedrockTextDisplayRenderer
import ink.pmc.visual.display.text.renderers.NmsTextDisplayRenderer
import ink.pmc.visual.toast.renderers.BedrockToastRenderer
import ink.pmc.visual.toast.renderers.NmsToastRenderer
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val bukkitModule = module {
    // Toast
    single<ToastFactory> { ToastFactoryImpl() }
    single<ToastRenderer<Player>>(named("nms")) { NmsToastRenderer() }
    single<ToastRenderer<Player>>(named("bedrock")) { BedrockToastRenderer() }

    // Display
    single<TextDisplayManager> { TextDisplayManagerImpl() }
    single<TextDisplayFactory> { TextDisplayFactoryImpl() }
    single<TextDisplayRenderer>(named("nms")) { NmsTextDisplayRenderer() }
    single<TextDisplayRenderer>(named("bedrock")) { BedrockTextDisplayRenderer() }
}

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), Listener {

    override suspend fun onEnableAsync() {
        startKoinIfNotPresent {
            modules(bukkitModule)
        }
        initialize()
    }

    private fun initialize() {
        server.pluginManager.registerSuspendingEvents(TextDisplayListener, this)
    }

}