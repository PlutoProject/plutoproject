package ink.pmc.transfer.scripting

class ProxyConfigureScopeImpl : ProxyConfigureScope {

    val conditions = mutableListOf<Condition>()

    override fun condition(scope: ConditionScopeDsl.() -> Unit) {
        conditions.addAll(ConditionScopeDsl().apply(scope).conditions)
    }

}