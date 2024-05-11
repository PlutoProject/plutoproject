package ink.pmc.common.member

import org.javers.core.diff.Diff

fun Diff.toJson(): String {
    return javers.jsonConverter.toJson(this)
}

fun String.toDiff(): Diff? {
    return try {
        javers.jsonConverter.fromJson(this, Diff::class.java)
    } catch (e: Exception) {
        null
    }
}