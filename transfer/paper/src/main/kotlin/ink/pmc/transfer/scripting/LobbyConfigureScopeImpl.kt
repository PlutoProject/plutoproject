package ink.pmc.transfer.scripting

class LobbyConfigureScopeImpl : LobbyConfigureScope {

    private var main: Menu? = null
    private val categoryMenus = mutableMapOf<String, Menu>()

    override fun menu(block: MenuScopeDsl.() -> Unit) {
        val scope = MenuScopeDsl().apply(block)
        this.main = scope.main
        this.categoryMenus.putAll(scope.categoryMenus)
    }

}