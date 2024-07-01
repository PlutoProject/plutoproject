package ink.pmc.transfer.scripting

import ink.pmc.utils.structure.Builder

@Suppress("UNUSED")
class MenuDsl : Builder<Menu> {

    private val structure = mutableListOf<String>()
    private var background: Char? = null
    private var closeButton: Char? = null
    private val destination = mutableMapOf<String, Char>()
    private val category = mutableMapOf<String, Char>()
    private var settings: Char? = null
    private var openHandler: ActionHandler = {}
    private var closeHandler: ActionHandler = {}

    fun structure(vararg data: String) {
        structure.addAll(data)
    }

    fun background(char: Char) {
        background = char
    }

    fun closeButton(char: Char) {
        closeButton = char
    }

    fun destination(name: String, char: Char) {
        destination[name] = char
    }

    fun category(name: String, char: Char) {
        category[name] = char
    }

    fun settings(char: Char) {
        settings = char
    }

    override fun build(): Menu {
        return Menu(
            structure,
            background,
            closeButton,
            closeButton,
            destination,
            category,
            settings,
            openHandler,
            closeHandler
        )
    }

}