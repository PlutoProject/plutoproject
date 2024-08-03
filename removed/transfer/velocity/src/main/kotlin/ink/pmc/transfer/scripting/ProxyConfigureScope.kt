package ink.pmc.transfer.scripting

interface ProxyConfigureScope {

    fun condition(scope: ConditionScopeDsl.() -> Unit)

}