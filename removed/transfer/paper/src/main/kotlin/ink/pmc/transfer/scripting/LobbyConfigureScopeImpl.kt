package ink.pmc.transfer.scripting

class LobbyConfigureScopeImpl : LobbyConfigureScope {

    var main: Menu? = null
    val categoryMenus = mutableMapOf<String, Menu>()

    override fun menu(block: MenuScopeDsl.() -> Unit) {
        val scope = MenuScopeDsl().apply(block)
        this.main = scope.main
        this.categoryMenus.putAll(scope.categoryMenus)
    }

}