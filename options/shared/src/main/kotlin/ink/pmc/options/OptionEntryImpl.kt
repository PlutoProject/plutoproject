package ink.pmc.options

import ink.pmc.options.api.EntryValueType
import ink.pmc.options.api.OptionDescriptor
import ink.pmc.options.api.OptionEntry

class OptionEntryImpl<T>(
    override val descriptor: OptionDescriptor<T>,
    override val value: T
) : OptionEntry<T> {
    override fun intValue(): Int {
        check(descriptor.type == EntryValueType.INT) { "${typeCheckMsg(descriptor.key)} Int" }
        return value as Int
    }

    override fun longValue(): Long {
        check(descriptor.type == EntryValueType.LONG) { "${typeCheckMsg(descriptor.key)} Long" }
        return value as Long
    }

    override fun shortValue(): Short {
        check(descriptor.type == EntryValueType.SHORT) { "${typeCheckMsg(descriptor.key)} Short" }
        return value as Short
    }

    override fun byteValue(): Byte {
        check(descriptor.type == EntryValueType.BYTE) { "${typeCheckMsg(descriptor.key)} Byte" }
        return value as Byte
    }

    override fun doubleValue(): Double {
        check(descriptor.type == EntryValueType.DOUBLE) { "${typeCheckMsg(descriptor.key)} Double" }
        return value as Double
    }

    override fun floatValue(): Float {
        check(descriptor.type == EntryValueType.FLOAT) { "${typeCheckMsg(descriptor.key)} Float" }
        return value as Float
    }

    override fun booleanValue(): Boolean {
        check(descriptor.type == EntryValueType.BOOLEAN) { "${typeCheckMsg(descriptor.key)} Boolean" }
        return value as Boolean
    }

    override fun stringValue(): String {
        check(descriptor.type == EntryValueType.STRING) { "${typeCheckMsg(descriptor.key)} String" }
        return value as String
    }

    override fun objectValue(): T {
        val objClass = descriptor.objectClass
        check(objClass != null) { "Object class cannot be null: ${descriptor.key}" }
        check(descriptor.type == EntryValueType.OBJECT && objClass.isInstance(value)) { "${typeCheckMsg(descriptor.key)} ${objClass.name}" }
        return value
    }

    private fun typeCheckMsg(key: String): String {
        return "Entry $key type not match:"
    }
}