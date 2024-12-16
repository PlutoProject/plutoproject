package ink.pmc.menu

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.sksamuel.hoplite.PropertySource
import ink.pmc.framework.utils.command.annotationParser
import ink.pmc.framework.utils.command.commandManager
import ink.pmc.framework.utils.config.preconfiguredConfigLoaderBuilder
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import ink.pmc.framework.utils.storage.saveResourceIfNotExisted
import ink.pmc.menu.api.MenuService
import ink.pmc.menu.api.factory.ButtonDescriptorFactory
import ink.pmc.menu.api.factory.PageDescriptorFactory
import ink.pmc.menu.button.*
import ink.pmc.menu.command.MenuCommand
import ink.pmc.menu.factory.ButtonDescriptorFactoryImpl
import ink.pmc.menu.factory.PageDescriptorFactoryImpl
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {
    private val config by inject<MenuConfig>()
    private val bukkitModule = module {
        single<MenuConfig> {
            preconfiguredConfigLoaderBuilder()
                .addPropertySource(PropertySource.file(saveResourceIfNotExisted("config.conf")))
                .build()
                .loadConfigOrThrow()
        }
        single<MenuService> { MenuServiceImpl() }
        single<PageDescriptorFactory> { PageDescriptorFactoryImpl() }
        single<ButtonDescriptorFactory> { ButtonDescriptorFactoryImpl() }
    }

    override suspend fun onEnableAsync() {
        plugin = this
        startKoinIfNotPresent {
            modules(bukkitModule)
        }
        commandManager().annotationParser().apply {
            parse(MenuCommand)
        }
    }

    private fun registerPrebuiltPages() {

    }

    private fun registerPrebuiltButtons() {
        if (config.prebuiltButtons.wiki) {
            MenuService.registerButton(WIKI_BUTTON_DESCRIPTOR, ::Wiki)
        }
        if (config.prebuiltButtons.inspect) {
            MenuService.registerButton(INSPECT_BUTTON_DESCRIPTOR, ::Inspect)
        }
        if (config.prebuiltButtons.balance) {
            MenuService.registerButton(BALANCE_BUTTON_DESCRIPTOR, ::Balance)
        }
    }
}