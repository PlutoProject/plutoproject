package ink.pmc.common.member.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.DataContainerStorage
import ink.pmc.common.utils.json.gson
import ink.pmc.common.utils.json.toJsonString
import kotlinx.coroutines.runBlocking
import java.time.Instant

class DataContainerImpl(private val service: AbstractMemberService, override val storage: DataContainerStorage) :
    AbstractDataContainer() {

    override val id: Long = storage.id
    override val owner: Member by lazy { runBlocking { service.lookup(storage.owner)!! } }
    override val createdAt: Instant = Instant.ofEpochMilli(storage.createdAt)
    override var lastModifiedAt: Instant = Instant.ofEpochMilli(storage.lastModifiedAt)
    override val contents: Map<String, String> = storage.contents

    override fun set(key: String, value: Any) {
        storage.contents[key] = value.toJsonString()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String, type: TypeToken<T>): T? {
        if (!contains(key)) {
            return null
        }

        return gson.fromJson(JsonParser.parseString(storage.contents[key]!!), type)
    }

    override fun get(key: String): JsonObject {
        return JsonParser.parseString(storage.contents[key]!!).asJsonObject
    }

    override fun getString(key: String): String? {
        return get(key).asJsonPrimitive.asString
    }

    override fun getByte(key: String): Byte {
        return get(key).asJsonPrimitive.asByte
    }

    override fun getShort(key: String): Short {
        return get(key).asJsonPrimitive.asShort
    }

    override fun getInt(key: String): Int {
        return get(key).asJsonPrimitive.asInt
    }

    override fun getLong(key: String): Long {
        return get(key).asJsonPrimitive.asLong
    }

    override fun getFloat(key: String): Float {
        return get(key).asJsonPrimitive.asFloat
    }

    override fun getDouble(key: String): Double {
        return get(key).asJsonPrimitive.asDouble
    }

    override fun getChar(key: String): Char {
        return get(key).asJsonPrimitive.asCharacter
    }

    override fun getBoolean(key: String): Boolean {
        return get(key).asJsonPrimitive.asBoolean
    }

    override fun contains(key: String): Boolean {
        return storage.contents.containsKey(key)
    }

}