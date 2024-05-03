package ink.pmc.common.member.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
    override val contents: MutableMap<String, String> = storage.contents

    override fun set(key: String, value: Any) {
        contents[key] = value.toJsonString()
    }

    override fun <T> get(key: String, type: Class<T>): T? {
        if (!contains(key)) {
            return null
        }

        return gson.fromJson(JsonParser.parseString(storage.contents[key]!!), type)
    }

    override fun get(key: String): JsonObject {
        return JsonParser.parseString(storage.contents[key]!!).asJsonObject
    }

    override fun remove(key: String) {
        contents.remove(key)
    }

    override fun getString(key: String): String? {
        return get(key).asJsonPrimitive.asString
    }

    override fun getByte(key: String): Byte? {
        return try {
            get(key).asJsonPrimitive.asByte
        } catch (e: Exception) {
            null
        }
    }

    override fun getShort(key: String): Short? {
        return try {
            get(key).asJsonPrimitive.asShort
        } catch (e: Exception) {
            null
        }
    }

    override fun getInt(key: String): Int? {
        return try {
            get(key).asJsonPrimitive.asInt
        } catch (e: Exception) {
            null
        }
    }

    override fun getLong(key: String): Long? {
        return try {
            get(key).asJsonPrimitive.asLong
        } catch (e: Exception) {
            null
        }
    }

    override fun getFloat(key: String): Float? {
        return try {
            get(key).asJsonPrimitive.asFloat
        } catch (e: Exception) {
            null
        }
    }

    override fun getDouble(key: String): Double? {
        return try {
            get(key).asJsonPrimitive.asDouble
        } catch (e: Exception) {
            null
        }
    }

    override fun getChar(key: String): Char? {
        return getString(key)?.get(0)
    }

    override fun getBoolean(key: String): Boolean {
        return try {
            get(key).asJsonPrimitive.asBoolean
        } catch (e: Exception) {
            false
        }
    }

    override fun contains(key: String): Boolean {
        return contents.containsKey(key)
    }

}