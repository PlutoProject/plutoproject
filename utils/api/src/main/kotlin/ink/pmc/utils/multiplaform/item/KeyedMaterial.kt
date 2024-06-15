package ink.pmc.utils.multiplaform.item

data class KeyedMaterial(val namespace: String, val key: String) {

    constructor(namespacedKey: String) : this(namespacedKey.substringBefore(':'), namespacedKey.substringAfter(':'))

}