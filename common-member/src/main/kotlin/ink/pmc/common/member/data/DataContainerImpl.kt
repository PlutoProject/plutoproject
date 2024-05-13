package ink.pmc.common.member.data

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.serverLogger
import ink.pmc.common.member.storage.DataContainerStorage
import ink.pmc.common.utils.json.gson
import ink.pmc.common.utils.storage.asBson
import ink.pmc.common.utils.storage.asObject
import org.bson.BsonDocument
import org.bson.BsonNull
import org.bson.BsonString
import org.bson.BsonValue
import java.time.Instant
import java.util.logging.Level

@Suppress("UNCHECKED_CAST")
class DataContainerImpl(override val owner: Member, override var storage: DataContainerStorage) :
    AbstractDataContainer() {

    override val id: Long = storage.id
    override val createdAt: Instant = Instant.ofEpochMilli(storage.createdAt)
    override var lastModifiedAt: Instant = Instant.ofEpochMilli(storage.lastModifiedAt)
    override val contents: BsonDocument = storage.contents.clone() // 复制原 Document，不要引用 storage 里的 Document

    override fun reload(storage: DataContainerStorage) {
        lastModifiedAt = Instant.ofEpochMilli(storage.lastModifiedAt)
        contents.clear()
        contents.putAll(storage.contents)
        this.storage = storage
    }

    override fun set(key: String, value: Any) {
        lastModifiedAt = Instant.now()
        contents[key] = value.asBson
    }

    override fun <T> get(key: String, type: Class<T>): T? {
        if (!contains(key)) {
            return null
        }

        return try {
            val str = (contents[key] as BsonString).value
            return gson.fromJson(str, type)
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to obtain a value from data container (key=$key) of member (uid=${owner.uid}, name=${owner.name})",
                e
            )
            null
        }
    }

    override fun get(key: String): BsonValue {
        return contents[key] ?: BsonNull()
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
        val array = contents.getArray(key) ?: return null

        return try {
            array.asObject as Collection<T>
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to obtain a collection value from data container (key=$key) of member (uid=${owner.uid}, name=${owner.name})"
            )
            null
        }
    }

    override fun <T> getMap(key: String, keyType: Class<T>): Map<String, T>? {
        val document = contents.getDocument(key) ?: return null

        return try {
            document.asObject as Map<String, T>
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to obtain a map value from data container (key=$key) of member (uid=${owner.uid}, name=${owner.name})"
            )
            null
        }
    }

    override fun contains(key: String): Boolean {
        return contents.containsKey(key)
    }

}