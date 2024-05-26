package ink.pmc.utils.json

import com.google.gson.Gson

inline fun <reified T> String.toObject(gsonInstance: Gson = gson): T {
    return gsonInstance.fromJson(this, T::class.java)
}

fun <T> String.toObject(type: Class<T>, gsonInstance: Gson = gson): T {
    return gsonInstance.fromJson(this, type)
}