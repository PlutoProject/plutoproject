package ink.pmc.common.refactor.member.api.data

import java.time.LocalDateTime

@Suppress("UNUSED")
abstract class History<T> {

    abstract var lastModifiedAt: LocalDateTime?
    private val contents: MutableList<Element<T>> = mutableListOf()

    val lastEntry: Element<T>?
        get() {
            if (lastModifiedAt == null || contents.lastOrNull() == null) {
                return null
            }

            return contents.last()
        }

    fun new(obj: T) {
        val date = LocalDateTime.now()
        val element = Element(date, obj)
        contents.add(element)
        lastModifiedAt = date
    }

    fun remove(index: Int) {
        contents.removeAt(index)
    }

    fun remove(time: LocalDateTime) {
        contents.removeIf { it.createdAt == time }
    }

    data class Element<T>(
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val content: T
    )

}