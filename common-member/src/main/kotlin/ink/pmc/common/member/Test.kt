package ink.pmc.common.member

import org.javers.core.JaversBuilder

fun main() {
    val javers = JaversBuilder.javers()
        .build()
    val old = Test()
    val new = Test(1, 0, listOf("111"))
    val diff = javers.compare(old, new)

    println(javers.jsonConverter.toJson(diff))
}

data class Test(val a: Int?, val b: Int?, val list: List<String>) {

    constructor() : this(null, null, listOf())

}