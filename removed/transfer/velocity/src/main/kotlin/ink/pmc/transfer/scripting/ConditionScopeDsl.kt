package ink.pmc.transfer.scripting

class ConditionScopeDsl {

    val conditions = mutableListOf<Condition>()

    fun destination(id: String, condition: ConditionDsl.() -> Unit) {
        conditions.add(ConditionDsl(id).apply(condition).build())
    }

}