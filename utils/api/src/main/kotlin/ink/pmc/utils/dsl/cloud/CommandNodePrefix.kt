package ink.pmc.utils.dsl.cloud

data class CommandNodePrefix(val name: String, val aliases: Array<String> = arrayOf()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandNodePrefix

        if (name != other.name) return false
        if (!aliases.contentEquals(other.aliases)) return false

        return true
    }

    infix fun alias(name: String): CommandNodePrefix {
        return copy(
            name = this.name,
            aliases = this.aliases.copyInto(arrayOf(name))
        )
    }

    infix fun alias(name: Array<String>): CommandNodePrefix {
        return copy(
            name = this.name,
            aliases = this.aliases.copyInto(name)
        )
    }

    infix fun alias(name: Collection<String>): CommandNodePrefix {
        return copy(
            name = this.name,
            aliases = this.aliases.copyInto(name.toTypedArray())
        )
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + aliases.contentHashCode()
        return result
    }

}

infix fun String.alias(name: String): CommandNodePrefix {
    return CommandNodePrefix(this).alias(name)
}

infix fun String.alias(name: Array<String>): CommandNodePrefix {
    return CommandNodePrefix(this).alias(name)
}

infix fun String.alias(name: Collection<String>): CommandNodePrefix {
    return CommandNodePrefix(this).alias(name)
}