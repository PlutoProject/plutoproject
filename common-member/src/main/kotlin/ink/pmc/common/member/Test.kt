package ink.pmc.common.member

fun main() {
/*
    val javers = JaversBuilder.javers().registerValueTypeAdapter(bsonDocumentAdapter)
        .build()

    val old = DataContainerStorage(
        objectId = ObjectId(),
        id = 1,
        owner = 1,
        createdAt = 1,
        lastModifiedAt = 1,
        contents = BsonDocument("a", BsonInt32(1))
    )
    val new = DataContainerStorage(
        objectId = ObjectId(),
        id = 1,
        owner = 1,
        createdAt = 1,
        lastModifiedAt = 1,
        contents = BsonDocument(listOf(BsonElement("1", BsonDocument("a", BsonInt64(123))), BsonElement("2", BsonBoolean(true))))
    )

    val diff = javers.compare(old, new)
    val json = javers.jsonConverter.toJson(diff)
    val decodeDiff = javers.jsonConverter.fromJson(json, Diff::class.java)

    println(decodeDiff.changes.filterIsInstance<ValueChange>().first().right)

    val double = BsonDouble(1.0).value
    println(double.toInt())*/
}

data class Test(val a: Int?, val b: Int?, val list: List<String>) {

    constructor() : this(121212, 1212121, listOf("1111"))

}