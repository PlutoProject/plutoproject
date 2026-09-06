package plutoproject.capability.flag.api

/**
 * 一个已注册的 flag 定义：名称、值类型、默认值和可选的说明文字。Redis 中的值以 [name] 为键存储。
 */
class FlagKey<T : Any>(
    val name: String,
    val type: FlagType<T>,
    val default: T,
    val description: String? = null,
)

fun booleanFlag(name: String, default: Boolean, description: String? = null) =
    FlagKey(name, FlagType.BOOLEAN, default, description)

fun intFlag(name: String, default: Int, description: String? = null) =
    FlagKey(name, FlagType.INT, default, description)

fun longFlag(name: String, default: Long, description: String? = null) =
    FlagKey(name, FlagType.LONG, default, description)

fun doubleFlag(name: String, default: Double, description: String? = null) =
    FlagKey(name, FlagType.DOUBLE, default, description)

fun stringFlag(name: String, default: String, description: String? = null) =
    FlagKey(name, FlagType.STRING, default, description)

/**
 * 把 [FlagKey] 的默认值序列化成字符串，供展示层使用。
 */
fun FlagKey<*>.serializeDefault(): String {
    @Suppress("UNCHECKED_CAST")
    val type = type as FlagType<Any>
    return type.serialize(default as Any)
}
