package ink.pmc.utils.data

fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> {
    require(fromIndex in 0..toIndex) { "Invalid indices" }

    if (fromIndex > size) return listOf()
    val size = this.size
    val endIndex = if (toIndex > size) size else toIndex

    return subList(fromIndex, endIndex)
}