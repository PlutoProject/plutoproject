package ink.pmc.framework.utils.item

data class KeyedMaterial(val namespace: String, val key: String) {

    val namespacedKey: String = "$namespace:$key"

    constructor(namespacedKey: String) : this(namespacedKey.substringBefore(':'), namespacedKey.substringAfter(':'))

}