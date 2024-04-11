package ink.pmc.common.refactor.member.api.data

import java.time.LocalDateTime
import java.util.UUID

@Suppress("UNUSED", "INAPPLICABLE_JVM_NAME")
interface DataEntry {

    val id: UUID
    val ownedBy: UUID
    val createdAt: LocalDateTime
    var lastModifiedAt: LocalDateTime?
    val contents: Map<String, Any>

    operator fun set(key: String, value: Any)

    operator fun get(key: String): Any?

    @JvmName("getWithType")
    operator fun <T> get(key: String): T?

    fun getString(key: String): String?

    fun getByte(key: String): Byte?

    fun getShort(key: String): Short?

    fun getInt(key: String): Int?

    fun getLong(key: String): Long?

    fun getFloat(key: String): Float?

    fun getDouble(key: String): Double?

    fun getChar(key: String): Char?

    fun getBoolean(key: String): Boolean

    suspend fun update()

    suspend fun refresh()

}