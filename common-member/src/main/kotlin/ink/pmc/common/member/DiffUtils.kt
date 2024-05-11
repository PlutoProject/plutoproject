package ink.pmc.common.member

import org.javers.core.diff.Diff

fun Diff.toJson(): String {
    return javers.jsonConverter.toJson(this)
}