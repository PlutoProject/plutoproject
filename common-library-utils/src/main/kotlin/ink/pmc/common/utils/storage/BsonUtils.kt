package ink.pmc.common.utils.storage

import ink.pmc.common.utils.json.toJsonString
import org.bson.*

val Any?.asBson: BsonValue
    get() {
        if (this == null) {
            return BsonNull()
        }

        return when(this) {
            is Byte -> BsonInt32(this.toInt())
            is Short -> BsonInt32(this.toInt())
            is Int -> BsonInt32(this)
            is Long -> BsonInt64(this)
            is Float -> BsonDouble(this.toDouble())
            is Double -> BsonDouble(this)
            is Char -> BsonString(this.toString())
            is Boolean -> BsonBoolean(this)
            is String -> BsonString(this)
            is Collection<*> -> BsonArray(this.map { it.asBson })
            is Map<*, *> -> BsonDocument(this.map { BsonElement(it.key.toString(), it.value.asBson) })
            else -> BsonString(this.toJsonString())
        }
    }

val BsonValue.asObject: Any?
    get() {
        return when(this) {
            is BsonInt32 -> this.value
            is BsonInt64 -> this.value
            is BsonDouble -> this.value
            is BsonString -> this.value
            is BsonBoolean -> this.value
            is BsonNull -> return null
            is BsonArray -> this.values.map { it.asObject }
            is BsonDocument -> this.entries.associate { it.key to it.value.asObject }
            else -> this.asString()
        }
    }