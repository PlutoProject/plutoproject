package ink.pmc.transfer.scripting

import ink.pmc.utils.multiplaform.player.PlayerWrapper

typealias ActionHandler = (player: PlayerWrapper<*>) -> Unit

data class Menu(
    val structure: List<String>,
    val background: Char?,
    val closeButton: Char?,
    val backButton: Char?,
    val destination: Map<String, Char>,
    val category: Map<String, Char>,
    val settings: Char?,
    val openHandler: ActionHandler,
    val closeHandler: ActionHandler
)