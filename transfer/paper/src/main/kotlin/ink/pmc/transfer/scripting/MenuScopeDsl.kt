package ink.pmc.transfer.scripting

@Suppress("UNUSED")
class MenuScopeDsl {

    var main: Menu? = null
    val categoryMenus = mutableMapOf<String, Menu>()

    fun main(menu: MenuDsl.() -> Unit) {
        main = MenuDsl().apply(menu).build()
    }

    fun category(id: String, menu: MenuDsl.() -> Unit) {
        categoryMenus[id] = MenuDsl().apply(menu).build()
    }

}