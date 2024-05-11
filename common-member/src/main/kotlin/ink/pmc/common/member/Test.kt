package ink.pmc.common.member

import ink.pmc.common.member.storage.DataContainerStorage
import org.bson.types.ObjectId
import org.javers.core.JaversBuilder

fun main() {
    val javers = JaversBuilder.javers().build()
    val origin = DataContainerStorage(
        ObjectId(),
        2,
        2,
        2,
        2,
        mutableMapOf(
            "a" to "a1"
        ),
    )
    val modified = DataContainerStorage(
        ObjectId(),
        4,
        4,
        4,
        4,
        mutableMapOf(
            "b" to "b",
            "c" to "c",
            "aaa" to Person("John", 111).toString()
        ),
    )
    val diff = modified.diff(origin)
    println(javers.jsonConverter.toJson(diff))
    origin.applyDiff(diff)
    println(origin)
}

data class Person(
    var name: String,
    var age: Int
)