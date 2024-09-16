package ink.pmc.playerdb

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.utils.inject.startKoinIfNotPresent
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.logging.Logger

private lateinit var pluginLogger: Logger
private val bukkitModule = module {
    single(named("player_database_logger")) { pluginLogger }
    single<Notifier> { BackendNotifier() }
}

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {

    override suspend fun onEnableAsync() {
        pluginLogger = logger
        startKoinIfNotPresent {
            modules(sharedModule, bukkitModule)
        }
        disabled = false
        get<Notifier>() // 初始化 Notifier
    }

    override suspend fun onDisableAsync() {
        disabled = true
        playerDbScope.cancel()
    }

}