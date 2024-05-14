package ink.pmc.common.member

import com.google.gson.*
import org.bson.*
import org.javers.core.json.JsonTypeAdapter
import org.jetbrains.annotations.ApiStatus.Experimental

/*
* TODO: 让 Javers 可以将 BsonDocument 视作 Map 来追踪条目的更改，而不是以 ValueChange 生成整个 Document 的 Diff。
* */

fun toJsonElement(bsonValue: BsonValue): JsonElement {
    return when (bsonValue) {
        is BsonInt32 -> JsonPrimitive(bsonValue.value)
        is BsonInt64 -> JsonPrimitive(bsonValue.value)
        is BsonDouble -> JsonPrimitive(bsonValue.value)
        is BsonString -> JsonPrimitive(bsonValue.value)
        is BsonBoolean -> JsonPrimitive(bsonValue.value)
        is BsonNull -> JsonNull.INSTANCE
        is BsonArray -> {
            JsonArray(bsonValue.size).apply {
                bsonValue.forEach {
                    add(toJsonElement(it))
                }
            }
        }

        is BsonDocument -> {
            JsonObject().apply {
                bsonValue.entries.forEach {
                    add(it.key, toJsonElement(it.value))
                }
            }
        }

        else -> {
            throw IllegalStateException("Type not supported: ${bsonValue::class.java.name}")
        }
    }
}

@Experimental
fun castPrimitive(primitive: JsonPrimitive): BsonValue {
    /*
    * 原思路来自 https://github.com/Kotlin/kotlinx.serialization/issues/1298#issuecomment-934427378
    * 经过修改后支持 Long。
    * */

    val intRegex = Regex("""^-?\d+$""")
    val longRegex = Regex("""^-?\d{10,}$""") // 至少 10 位数字，可能是 Long
    val doubleRegex = Regex("""^-?\d+\.\d+(?:[eE][-+]?\d+)?$""") // 支持科学计数法

    return when {
        primitive.isString -> {
            BsonString(primitive.asString)
        }

        primitive.isBoolean -> {
            BsonBoolean(primitive.asBoolean)
        }

        primitive.isNumber -> {
            return when {
                longRegex.matches(primitive.asString) -> BsonInt64(primitive.asLong)
                intRegex.matches(primitive.asString) -> BsonInt32(primitive.asInt)
                doubleRegex.matches(primitive.asString) -> BsonDouble(primitive.asDouble)
                else -> {
                    throw IllegalStateException("Type not supported: $primitive")
                }
            }
        }

        else -> {
            throw IllegalStateException("Type not supported: $primitive")
        }
    }
}

fun fromJsonElement(element: JsonElement): BsonValue {
    return when (element) {
        is JsonPrimitive -> castPrimitive(element)
        is JsonNull -> BsonNull()
        is JsonArray -> {
            BsonArray(element.size()).apply {
                element.forEach {
                    add(fromJsonElement(it))
                }
            }
        }

        is JsonObject -> {
            BsonDocument().apply {
                element.asMap().entries.forEach {
                    put(it.key, fromJsonElement(it.value))
                }
            }
        }

        else -> {
            throw IllegalStateException("Type not supported: $element")
        }
    }
}

val bsonDocumentAdapter = object : JsonTypeAdapter<BsonDocument> {
    override fun fromJson(
        json: JsonElement,
        jsonDeserializationContext: JsonDeserializationContext?
    ): BsonDocument {
        return fromJsonElement(json) as BsonDocument
    }

    override fun getValueTypes(): MutableList<Class<BsonDocument>> {
        return mutableListOf(BsonDocument::class.java)
    }

    override fun toJson(
        sourceValue: BsonDocument,
        jsonSerializationContext: JsonSerializationContext
    ): JsonElement {
        return toJsonElement(sourceValue)
    }
}