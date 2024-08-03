package ink.pmc.transfer.scripting

interface LobbyConfigureScope {

    fun menu(block: MenuScopeDsl.() -> Unit)

}