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
    override var contents: BsonDocument = storage.contents.clone() // 复制原 Document，不要引用 storage 里的 Document

    override fun reload(storage: DataContainerStorage) {
        lastModifiedAt = Instant.ofEpochMilli(storage.lastModifiedAt)
        contents = storage.contents.clone()
        this.storage = storage
    }

    override fun toStorage(): DataContainerStorage {
        return storage.copy(
            id = this.id,
            owner = this.owner.uid,
            createdAt = this.createdAt.toEpochMilli(),
            lastModifiedAt = this.lastModifiedAt.toEpochMilli(),
            contents = this.contents.clone(),
            new = false
        )
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
            val str = contents[key]!!.asString().value
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
        if (!contains(key)) {
            return null
        }

        return contents.getString(key)?.value
    }

    override fun getByte(key: String): Byte? {
        if (!contains(key)) {
            return null
        }

        return contents.getString(key)?.value?.toByteOrNull()
    }

    override fun getShort(key: String): Short? {
        if (!contains(key)) {
            return null
        }

        return contents.getInt32(key)?.value?.toShort()
    }

    override fun getInt(key: String): Int? {
        if (!contains(key)) {
            return null
        }

        return contents.getInt32(key)?.value
    }

    override fun getLong(key: String): Long? {
        if (!contains(key)) {
            return null
        }

        return contents.getInt64(key)?.value
    }

    override fun getFloat(key: String): Float? {
        if (!contains(key)) {
            return null
        }

        return contents.getDouble(key)?.value?.toFloat()
    }

    override fun getDouble(key: String): Double? {
        if (!contains(key)) {
            return null
        }

        return contents.getDouble(key)?.value
    }

    override fun getChar(key: String): Char? {
        if (!contains(key)) {
            return null
        }

        return contents.getString(key)?.value?.toCharArray()?.get(0)
    }

    override fun getBoolean(key: String): Boolean {
        if (!contains(key)) {
            return false
        }

        return contents.getBoolean(key)?.value ?: false
    }

    override fun <T> getCollection(key: String, type: Class<T>): Collection<T>? {
        if (!contains(key)) {
            return null
        }

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
        if (!contains(key)) {
            return null
        }

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