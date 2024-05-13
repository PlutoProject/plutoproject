package ink.pmc.common.member

import com.google.gson.Gson
import ink.pmc.common.utils.json.gson
import org.bson.BsonDocument
import org.bson.BsonInt64
import org.bson.BsonJavaScript
import org.bson.BsonValue
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.MapChange

fun main() {
/*    val javers = JaversBuilder.javers()
        .build()
    val old = Test()
    val new = Test(1, 0, listOf("111"), BsonDocument("a", BsonInt64(1)))
    val diff = javers.compare(old, new)
    val doc = BsonDocument()

    diff.changes.filterIsInstance<MapChange<*>>().forEach { mapChange ->
        mapChange.entryChanges.filterIsInstance<EntryAdded>().forEach {
            doc[it.key as String] = it.value as BsonValue
        }
    }

    println(doc)

    println(javers.jsonConverter.toJson(diff))*/
/*    val gson = Gson()
    val str = gson.toJson(Test())
    println(str)
    println(gson.fromJson(str, Any::class.java) as Test)*/
}

data class Test(val a: Int?, val b: Int?, val list: List<String>) {

    constructor() : this(121212, 1212121, listOf("1111"))

}