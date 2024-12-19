package ink.pmc.framework.storage

import ink.pmc.framework.json.toJsonString
import org.bson.*

val Any?.asBson: BsonValue
    get() {
        if (this == null) return BsonNull()
        return when (this) {
            is Byte -> BsonInt32(this.toInt())
            is Short -> BsonInt32(this.toInt())
            is Int -> BsonInt32(this)
            is Long -> BsonInt64(this)
            is Float -> BsonDouble(this.toDouble())
            is Double -> BsonDouble(this)
            is Char -> BsonString(this.toString())
            is Boolean -> BsonBoolean(this)
            is String -> BsonString(this)
            else -> BsonString(this.toJsonString())
        }
    }

val BsonValue.asObject: Any?
    get() {
        return when (this) {
            is BsonInt32 -> this.value
            is BsonInt64 -> this.value
            is BsonDouble -> this.value
            is BsonString -> this.value
            is BsonBoolean -> this.value
            is BsonNull -> return null
            is BsonArray -> this.values.map { it.asObject }
            is BsonDocument -> this.entries.associate { it.key to it.value.asObject }
            else -> return null
        }
    }

val BsonValue.string: String
    get() {
        return when (this) {
            is BsonInt32 -> this.value.toString()
            is BsonInt64 -> this.value.toString()
            is BsonDouble -> this.value.toString()
            is BsonString -> this.value
            is BsonBoolean -> this.value.toString()
            is BsonNull -> return "null"
            is BsonArray -> this.values.map { it.asObject }.toJsonString()
            is BsonDocument -> this.entries.associate { it.key to it.value.asObject }.toJsonString()
            else -> return "null"
        }
    }