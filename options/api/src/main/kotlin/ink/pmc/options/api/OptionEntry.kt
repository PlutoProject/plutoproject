package ink.pmc.options.api

interface OptionEntry<T> {
    val key: String
    val value: T

    fun intValue(): Int

    fun longValue(): Long

    fun shortValue(): Short

    fun byteValue(): Byte

    fun doubleValue(): Double

    fun floatValue(): Float

    fun booleanValue(): Boolean

    fun stringValue(): String

    fun <T> objectValue(): T
}