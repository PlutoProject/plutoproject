package ink.pmc.common.utils.json

import com.google.gson.JsonObject

inline fun <reified T> JsonObject.toObject(): T {
    return gson.fromJson(this, T::class.java)
}