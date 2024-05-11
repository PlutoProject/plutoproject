package ink.pmc.common.member.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.DataContainerStorage
import ink.pmc.common.utils.json.toJsonString
import ink.pmc.common.utils.json.toObject
import java.time.Instant

@Suppress("UNCHECKED_CAST")
class DataContainerImpl(override val owner: Member, override val storage: DataContainerStorage) :
    AbstractDataContainer() {

    override val id: Long = storage.id
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

        return try {
            storage.contents[key]!!.toObject(type)
        } catch (e: Exception) {
            null
        }
    }

    override fun get(key: String): JsonObject {
        return JsonParser.parseString(storage.contents[key]!!).asJsonObject
    }

    override fun remove(key: String) {
        contents.remove(key)
    }

    override fun getString(key: String): String? {
        return get(key, String::class.java)
    }

    override fun getByte(key: String): Byte? {
        return get(key, Byte::class.java)
    }

    override fun getShort(key: String): Short? {
        return get(key, Short::class.java)
    }

    override fun getInt(key: String): Int? {
        return get(key, Int::class.java)
    }

    override fun getLong(key: String): Long? {
        return get(key, Long::class.java)
    }

    override fun getFloat(key: String): Float? {
        return get(key, Float::class.java)
    }

    override fun getDouble(key: String): Double? {
        return get(key, Double::class.java)
    }

    override fun getChar(key: String): Char? {
        return get(key, Char::class.java)
    }

    override fun getBoolean(key: String): Boolean {
        return get(key, Boolean::class.java) ?: false
    }

    override fun <T> getCollection(key: String, type: Class<T>): Collection<T>? {
        return try {
            val obj = get(key, Any::class.java)
            if (obj is Collection<*>) {
                obj as Collection<T>
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun contains(key: String): Boolean {
        return contents.containsKey(key)
    }

}