package ink.pmc.common.member

import org.javers.core.JaversBuilder

fun main() {
    val javers = JaversBuilder.javers().build()
    val original = Person("John", 30, listOf("a", "b"))
    val modified = Person("John", 30, listOf("a", "c", "b"))

    val diff = javers.compare(original, modified)
    println(javers.jsonConverter.toJson(diff))
}

data class Person(
    var name: String,
    var age: Int,
    var list: Collection<String>
)