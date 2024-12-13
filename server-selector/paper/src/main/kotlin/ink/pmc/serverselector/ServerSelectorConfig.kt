package ink.pmc.serverselector

import net.kyori.adventure.text.Component
import org.bukkit.Material

data class ServerSelectorConfig(
    val menu: Menu = Menu(),
    val server: Map<String, Server> = mapOf()
)

data class Menu(
    val rows: Int = 3,
    val pattern: List<String> = listOf(),
    val ingredient: Map<String, Ingredient> = mapOf(),
)

data class Ingredient(
    val material: Material = Material.PAPER,
)

data class Server(
    val menuIcon: String,
    val name: String,
    val displayName: Component = Component.text(name),
    val description: List<Component> = listOf()
)