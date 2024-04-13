package ink.pmc.common.member.data

import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.DataContainerStorage
import kotlinx.coroutines.runBlocking
import java.time.Instant

class DataContainerImpl(private val service: AbstractMemberService, override val storage: DataContainerStorage) :
    AbstractDataContainer() {

    override val id: Long = storage.id
    override val owner: Member = runBlocking { service.lookup(storage.owner)!! }
    override val createdAt: Instant = Instant.ofEpochMilli(storage.createdAt)
    override var lastModifiedAt: Instant = Instant.ofEpochMilli(storage.lastModifiedAt)
    override val contents: Map<String, Any> = storage.contents

    override fun set(key: String, value: Any) {
        storage.contents[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): T? {
        if (!contains(key)) {
            return null
        }

        return storage.contents[key] as T
    }

    override fun getString(key: String): String? {
        return get(key)
    }

    override fun getByte(key: String): Byte? {
        return get(key)
    }

    override fun getShort(key: String): Short? {
        return get(key)
    }

    override fun getInt(key: String): Int? {
        return get(key)
    }

    override fun getLong(key: String): Long? {
        return get(key)
    }

    override fun getFloat(key: String): Float? {
        return get(key)
    }

    override fun getDouble(key: String): Double? {
        return get(key)
    }

    override fun getChar(key: String): Char? {
        return get(key)
    }

    override fun getBoolean(key: String): Boolean {
        if (get<Boolean>(key) == null) {
            return false
        }

        return get(key)!!
    }

    override fun contains(key: String): Boolean {
        return storage.contents.containsKey(key)
    }

}