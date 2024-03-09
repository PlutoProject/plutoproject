package ink.pmc.common.member.api

import java.util.*

@Suppress("UNCHECKED_CAST")
interface MemberData {

    val data: MutableMap<String, Any>

    operator fun get(key: String, default: Any? = null): Any? {
        if (!data.containsKey(key)) {
            return default
        }

        return data[key]
    }

    fun remove(key: String) {
        data.remove(key)
    }

    operator fun set(key: String, value: Any?) {
        if (value == null) {
            remove(key)
        }

        data[key] = value!!
    }

    fun contains(key: String): Boolean {
        return data.containsKey(key)
    }

    fun notContains(key: String): Boolean {
        return !data.containsKey(key)
    }

    fun getString(key: String, default: String? = null): String? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is String) {
            return this[key] as String
        }

        return null
    }

    fun getShort(key: String, default: Short? = null): Short? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Short) {
            return this[key] as Short
        }

        return null
    }

    fun getInt(key: String, default: Int? = null): Int? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Int) {
            return this[key] as Int
        }

        return null
    }

    fun getLong(key: String, default: Long? = null): Long? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Long) {
            return this[key] as Long
        }

        return null
    }

    fun getFloat(key: String, default: Float? = null): Float? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Float) {
            return this[key] as Float
        }

        return null
    }

    fun getDouble(key: String, default: Double? = null): Double? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Double) {
            return this[key] as Double
        }

        return null
    }

    fun getBoolean(key: String, default: Boolean? = null): Boolean? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Boolean) {
            return this[key] as Boolean
        }

        return null
    }

    fun getChar(key: String, default: Char? = null): Char? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Char) {
            return this[key] as Char
        }

        return null
    }

    fun getByte(key: String, default: Byte? = null): Byte? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Byte) {
            return this[key] as Byte
        }

        return null
    }

    fun getUUID(key: String, default: UUID? = null): UUID? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is UUID) {
            return this[key] as UUID
        }

        return null
    }

    fun <T> getCollection(key: String, default: Collection<T>? = null): Collection<T>? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Collection<*>) {
            return this[key] as? Collection<T>
        }

        return null
    }

    fun <T> getList(key: String, default: List<T>? = null): List<T>? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is List<*>) {
            return this[key] as? List<T>
        }

        return null
    }

    fun <T> getSet(key: String, default: Set<T>? = null): Set<T>? {
        if (notContains(key)) {
            return default
        }

        if (this[key] is Set<*>) {
            return this[key] as? Set<T>
        }

        return null
    }

}