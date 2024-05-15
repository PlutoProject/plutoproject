package ink.pmc.common.member.storage

interface Storable<T> {

    fun reload(storage: T)

    fun createBean(): T

}