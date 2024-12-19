package ink.pmc.framework.options.models

import ink.pmc.framework.options.EntryValueType
import ink.pmc.framework.options.EntryValueType.*
import ink.pmc.framework.options.OptionEntry
import ink.pmc.framework.options.UnknownDescriptor
import ink.pmc.framework.json.gson
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
internal fun OptionEntry<*>.toModel(): OptionEntryModel {
    return OptionEntryModel(descriptor.key, when (descriptor.type) {
        INT -> Json.encodeToString(value as Int)
        LONG -> Json.encodeToString(value as Long)
        SHORT -> Json.encodeToString(value as Short)
        BYTE -> Json.encodeToString(value as Byte)
        DOUBLE -> Json.encodeToString(value as Double)
        FLOAT -> Json.encodeToString(value as Float)
        BOOLEAN -> Json.encodeToString(value as Boolean)
        STRING -> Json.encodeToString(value as String)
        OBJECT -> {
            val objClass = checkNotNull(descriptor.objectClass) { "Object class cannot be null: ${descriptor.key}" }
            val kSerializer = objClass.kotlin.serializerOrNull()
            if (kSerializer != null) {
                Json.encodeToString(kSerializer as KSerializer<Any>, value)
            } else {
                gson.toJson(value, objClass)
            }
        }

        UNKNOWN -> value as String
    }, if (descriptor.type == UNKNOWN) {
        (descriptor as UnknownDescriptor).originalType
    } else descriptor.type)
}

@Serializable
data class OptionEntryModel(
    val key: String,
    val value: String,
    val type: EntryValueType
)