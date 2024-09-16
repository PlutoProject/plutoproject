package ink.pmc.member.data

import ink.pmc.member.api.Member
import ink.pmc.member.serverLogger
import ink.pmc.member.storage.DataContainerBean
import org.bson.*
import java.time.Instant
import java.util.logging.Level

@Suppress("UNCHECKED_CAST")
class DataContainerImpl(override val owner: Member, override var bean: DataContainerBean) :
    AbstractDataContainer() {

    override val id: Long = bean.id
    override val createdAt: Instant = Instant.ofEpochMilli(bean.createdAt)
    override var lastModifiedAt: Instant = Instant.ofEpochMilli(bean.lastModifiedAt)
    override var contents: BsonDocument = bean.contents.clone() // 复制原 Document，不要引用 bean 里的 Document

    override fun reload(storage: DataContainerBean) {
        lastModifiedAt = Instant.ofEpochMilli(storage.lastModifiedAt)
        contents = storage.contents.clone()
        this.bean = storage
    }

    override fun createBean(): DataContainerBean {
        return bean.copy(
            id = this.id,
            owner = this.owner.uid,
            createdAt = this.createdAt.toEpochMilli(),
            lastModifiedAt = this.lastModifiedAt.toEpochMilli(),
            contents = this.contents.clone(),
            new = false
        )
    }

    private fun toBson(obj: Any?): BsonValue {
        if (obj == null) {
            return BsonNull()
        }

        return when (obj) {
            is Byte -> BsonInt32(obj.toInt())
            is Short -> BsonInt32(obj.toInt())
            is Int -> BsonInt32(obj)
            is Long -> BsonInt64(obj)
            is Float -> BsonDouble(obj.toDouble())
            is Double -> BsonDouble(obj)
            is Char -> BsonString(obj.toString())
            is Boolean -> BsonBoolean(obj)
            is String -> BsonString(obj)
            is Collection<*> -> BsonArray(obj.filterNotNull().map { toBson(it) })
            is Map<*, *> -> BsonDocument(obj.entries.filter { it.key is String && it.value != null }
                .map { BsonElement(it.key as String, toBson(it.value!!)) })

            else -> {
                throw IllegalStateException("Type not supported: ${obj::class.java.name}")
            }
        }
    }

    private fun fromBson(value: BsonValue?): Any? {
        if (value == null) {
            return null
        }

        return when (value) {
            is BsonInt32 -> value.value
            is BsonInt64 -> value.value
            is BsonDouble -> value.value
            is BsonString -> value.value
            is BsonBoolean -> value.value
            is BsonNull -> return null
            is BsonArray -> return value.values.map { fromBson(it) }
            is BsonDocument -> return value.entries.associate { it.key to fromBson(it.value) }
            else -> {
                throw IllegalStateException("Type not supported: ${value::class.java.name}")
            }
        }
    }

    private fun isNestedKey(key: String): Boolean {
        return key.contains('.')
    }

    private fun isLegalNestedKey(key: String): Boolean {
        return !key.startsWith('.') && !key.endsWith('.')
    }

    private fun throwIfIllegalNestedKey(key: String) {
        if (!isLegalNestedKey(key)) {
            throw IllegalStateException("Illegal key: $key")
        }
    }

    private fun setNested(key: String, value: BsonValue) {
        throwIfIllegalNestedKey(key)

        val keys = key.split('.')
        val range = keys.indices
        val last = range.last
        var curr = contents

        for (i in range) {
            if (i == last) {
                curr[keys[i]] = value
                break
            }

            val next = curr.computeIfAbsent(keys[i]) { BsonDocument() }

            if (next !is BsonDocument) {
                throw IllegalStateException("Key ${keys.subList(0, i + 1).joinToString(".")} isn't BsonDocument")
            }

            curr = next
        }
    }

    private fun getNested(key: String): BsonValue? {
        throwIfIllegalNestedKey(key)

        val keys = key.split('.')
        val range = keys.indices
        val last = range.last
        var curr = contents

        for (i in range) {
            if (i == last) {
                return curr[keys[i]]
            }

            val next = curr[keys[i]] ?: return null

            if (next !is BsonDocument) {
                throw IllegalStateException("Key ${keys.subList(0, i + 1).joinToString(".")} isn't BsonDocument")
            }

            curr = next
        }

        return null
    }


    private fun removeNested(key: String) {
        throwIfIllegalNestedKey(key)

        val keys = key.split('.')
        val range = keys.indices
        val last = range.last
        var curr = contents

        for (i in range) {
            if (i == last) {
                curr.remove(keys[i])
                break
            }

            val next = curr[keys[i]] ?: return

            if (next !is BsonDocument) {
                throw IllegalStateException("Key ${keys.subList(0, i + 1).joinToString(".")}} isn't BsonDocument")
            }

            curr = next
        }
    }

    private fun containsNested(key: String): Boolean {
        return getNested(key) != null
    }

    override fun set(key: String, value: Any) {
        try {
            setBson(key, toBson(value))
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to set a value in UID ${owner.uid}'s data container (key=$key, value=$value)",
                e
            )
        }
    }

    override fun get(key: String): Any? {
        try {
            return fromBson(getBson(key))
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to get a value in UID ${owner.uid}'s data container (key=$key)",
                e
            )

            return null
        }
    }

    override fun <T : Any> computeIfAbsent(key: String, compute: (String) -> T): T {
        if (contains(key)) return get(key) as T
        val computed = compute(key)
        set(key, computed)
        return computed
    }

    override fun setBson(key: String, value: BsonValue) {
        try {
            lastModifiedAt = Instant.now()

            if (isNestedKey(key)) {
                setNested(key, value)
                return
            }

            contents[key] = value
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to set a value in UID ${owner.uid}'s data container (key=$key, value=$value)",
                e
            )
        }
    }

    override fun getBson(key: String): BsonValue? {
        try {
            if (isNestedKey(key)) {
                return getNested(key)
            }

            return contents[key]
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to get a value in UID ${owner.uid}'s data container (key=$key)",
                e
            )

            return null
        }
    }

    override fun remove(key: String) {
        try {
            if (isNestedKey(key)) {
                removeNested(key)
                return
            }

            contents.remove(key)
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to remove a value in UID ${owner.uid}'s data container (key=$key)",
                e
            )
        }
    }

    override fun getString(key: String): String? {
        return get(key) as String?
    }

    override fun getByte(key: String): Byte? {
        return getString(key)?.toByte()
    }

    override fun getShort(key: String): Short? {
        return getInt(key)?.toShort()
    }

    override fun getInt(key: String): Int? {
        return get(key) as Int?
    }

    override fun getLong(key: String): Long? {
        if (get(key) is Int) {
            return getInt(key)?.toLong()
        }

        return get(key) as Long?
    }

    override fun getFloat(key: String): Float? {
        return getDouble(key)?.toFloat()
    }

    override fun getDouble(key: String): Double? {
        return get(key) as Double?
    }

    override fun getChar(key: String): Char? {
        return getString(key)?.get(0)
    }

    override fun getBoolean(key: String): Boolean {
        return get(key) as Boolean? ?: false
    }

    override fun <T> getList(key: String): List<*>? {
        return try {
            get(key) as List<T>
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to get a list in UID ${owner.uid}'s data container (key=$key)",
                e
            )

            null
        }
    }

    override fun <T> getMap(key: String): Map<String, *>? {
        return try {
            get(key) as Map<String, T>
        } catch (e: Exception) {
            serverLogger.log(
                Level.SEVERE,
                "Failed to get a map in UID ${owner.uid}'s data container (key=$key)",
                e
            )

            null
        }
    }

    override fun contains(key: String): Boolean {
        if (isNestedKey(key)) {
            return containsNested(key)
        }

        return contents.containsKey(key)
    }

}