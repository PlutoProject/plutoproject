package ink.pmc.member.storage

interface Storable<T> {

    fun reload(storage: T)

    fun createBean(): T

}