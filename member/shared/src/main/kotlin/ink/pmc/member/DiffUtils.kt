package ink.pmc.member

import org.javers.core.diff.Diff

fun Diff.toJson(): String {
    return javers.jsonConverter.toJson(this)
}

fun String.toDiff(): Diff? {
    return javers.jsonConverter.fromJson(this, Diff::class.java)
}