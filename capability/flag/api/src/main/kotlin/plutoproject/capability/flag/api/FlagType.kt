package plutoproject.capability.flag.api

/**
 * flag 的值类型，负责在值与 Redis 中存储的字符串形式之间相互转换。
 */
sealed class FlagType<T : Any>(val id: String) {
    data object BOOLEAN : FlagType<Boolean>("boolean") {
        override fun serialize(value: Boolean): String = value.toString()

        override fun deserialize(raw: String): Boolean? = when (raw.lowercase()) {
            "true" -> true
            "false" -> false
            else -> null
        }
    }

    data object INT : FlagType<Int>("int") {
        override fun serialize(value: Int): String = value.toString()

        override fun deserialize(raw: String): Int? = raw.toIntOrNull()
    }

    data object LONG : FlagType<Long>("long") {
        override fun serialize(value: Long): String = value.toString()

        override fun deserialize(raw: String): Long? = raw.toLongOrNull()
    }

    data object DOUBLE : FlagType<Double>("double") {
        override fun serialize(value: Double): String = value.toString()

        override fun deserialize(raw: String): Double? = raw.toDoubleOrNull()
    }

    data object STRING : FlagType<String>("string") {
        override fun serialize(value: String): String = value

        override fun deserialize(raw: String): String = raw
    }

    abstract fun serialize(value: T): String

    abstract fun deserialize(raw: String): T?
}
