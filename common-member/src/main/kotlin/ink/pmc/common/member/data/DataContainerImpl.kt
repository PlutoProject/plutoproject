package ink.pmc.common.member.data

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.DataContainerStorage
import org.bson.Document
import java.time.Instant

@Suppress("UNCHECKED_CAST")
class DataContainerImpl(override val owner: Member, override var storage: DataContainerStorage) :
    AbstractDataContainer() {

    override val id: Long = storage.id
    override val createdAt: Instant = Instant.ofEpochMilli(storage.createdAt)
    override var lastModifiedAt: Instant = Instant.ofEpochMilli(storage.lastModifiedAt)
    override val contents: Document = storage.contents // 复制原 Map，不要引用 storage 里的 Map

    override fun reload(storage: DataContainerStorage) {
        lastModifiedAt = Instant.ofEpochMilli(storage.lastModifiedAt)
        contents.clear()
        contents.putAll(storage.contents)
        this.storage = storage
    }

    override fun set(key: String, value: Any) {
        lastModifiedAt = Instant.now()
        contents[key] = value
    }

    override fun <T> get(key: String, type: Class<T>): T? {
        if (!contains(key)) {
            return null
        }

        return try {
            storage.contents[key]!! as T?
        } catch (e: Exception) {
            null
        }
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
            val obj = get(key, ArrayList::class.java)
            if (obj is Collection<*>) {
                obj as Collection<T>
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun contains(key: String): Boolean {
        return contents.containsKey(key)
    }

}