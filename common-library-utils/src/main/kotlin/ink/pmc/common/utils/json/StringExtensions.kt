package ink.pmc.common.utils.json

inline fun <reified T> String.toObject(): T {
    return gson.fromJson(this, T::class.java)
}